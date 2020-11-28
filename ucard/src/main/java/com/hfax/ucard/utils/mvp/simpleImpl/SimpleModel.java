package com.hfax.ucard.utils.mvp.simpleImpl;

import android.content.Context;
import android.content.Intent;

import com.hfax.app.BaseActivity;
import com.hfax.app.BaseFragment;
import com.hfax.app.h5.MaintenanceActivity;
import com.hfax.app.utils.EventBusUtils;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.gson.converter.GsonConverterFactory;
import com.hfax.lib.network.BaseResponse;
import com.hfax.lib.network.CommonParamInteceptor;
import com.hfax.lib.network.RetrofitUtil;
import com.hfax.lib.utils.GsonUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.modules.entrance.UCardApplication;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.user.LoginActivity;
import com.hfax.ucard.bean.ImageCodeBean;
import com.hfax.ucard.utils.FileUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.YunPianUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.DefaultTSubscriber;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.NetworkService;
import com.hfax.ucard.utils.mvp.OnLoadDataListener;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.RetrofitStringUtil;
import com.hfax.ucard.utils.mvp.simpleImpl.download.DownloadInteceptor;
import com.hfax.ucard.utils.mvp.simpleImpl.download.OnDownloadListener;
import com.hfax.ucard.widget.codes.ImageCodeDialog;
import com.hfax.ucard.widget.codes.ImageCodeManager;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SimpleModel {
    static volatile Retrofit downLoadRetrofit;
    Map<String, Subscription> caches = new HashMap<>();
    private static final int ERR_CODE_SYS_MAINTENANCE = 800004;
    private static final int ERR_CODE_USER_LOGOUT = 800003;
    private static long lastStart;

    /**
     * 加载数据
     */
    public <T> void doLoadData(final RequestMap map, final Object provider, final OnLoadDataListener<T> listener, final MVPUtils.Method method, final Type type) {
        doLoadTData(map, provider, new SimpleLoadDataListener<BaseResponse<T>>() {
            @Override
            public void onSuccess(BaseResponse<T> response) {
                if (listener != null) {
                    if (response.isSuccess()) {
                        listener.onSuccess(response.data);
                    } else {
                        onFail(response.errCode, response.errMsg);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                if (NetworkAddress.CODE_ERROR_MSG_INPUT == code) {
                    final BaseActivity activity;
                    if (provider instanceof BaseActivity) {
                        activity = (BaseActivity) provider;
                    } else if (provider instanceof BaseFragment) {
                        activity = (BaseActivity) ((BaseFragment) provider).getActivity();
                    } else {
                        activity = null;
                    }
                    if (activity != null) {
                        activity.dismissLoadingDialog();
                        ImageCodeManager.requestImgCode(activity, new ImageCodeDialog.CusOnClickListener() {
                            @Override
                            public void onClick(ImageCodeBean imageCodeBean, String text) {
                                map.put("captcha-id", imageCodeBean.captchaId);
                                map.put("captcha-text", text);
                                doLoadData(map, provider, listener, method, type);
                                activity.showLoadingDialog();
                            }

                            @Override
                            public void onCancel() {
                                activity.dismissLoadingDialog();
                            }
                        });
                        return;
                    }

                } else if (code == NetworkAddress.CODE_ERROR_YUN_PIAN) {
                    final BaseActivity activity;
                    if (provider instanceof BaseActivity) {
                        activity = (BaseActivity) provider;
                    } else if (provider instanceof BaseFragment) {
                        activity = (BaseActivity) ((BaseFragment) provider).getActivity();
                    } else {
                        activity = null;
                    }
                    if (activity != null) {
                        activity.dismissLoadingDialog();
                        YunPianUtils.getYunPian().clear();
                        YunPianUtils.getYunPian().requestYunPian(activity, new DataChange<Map<String, String>>() {
                            @Override
                            public void onChange(Map<String, String> stringStringMap) {
                                if (stringStringMap != null) {
                                    map.putAll(stringStringMap);
                                    activity.showLoadingDialog();
                                    doLoadData(map, provider, listener, method, type);
                                } else {
                                    if(listener!=null){
                                        listener.onFail(-2, null);
                                    }

                                }
                            }
                        });
                        return;
                    }
                }
                if (!processErrCode(code, msg, provider)) {
                    if (listener != null) {
                        listener.onFail(code, msg);
                    }
                }
            }
        }, method, new Func1<BaseResponse, BaseResponse<T>>() {
            @Override
            public BaseResponse<T> call(BaseResponse response) {
                if (response != null && response.data != null) {
                    String data = GsonUtils.bean2Json(response.data);
                    if (String.class.equals(type)) {
                        response.data = data;
                    } else {
                        response.data = GsonUtils.json2Bean(data, type);
                        if (response.data == null) {
                            response.errCode = BaseResponse.RESPONSE_ERROR;
                            response.errMsg = "数据解析失败";
                        }
                    }
                }
                return response;
            }
        });
    }

    /**
     * 加载数据
     */
    protected <T> void doLoadTData(final RequestMap map, Object provider, final SimpleLoadDataListener<T> listener, MVPUtils.Method method, Func1... funcs) {
        if (!Utils.isConnectNetWork(BaseApplication.getContext())) {
            onFail(listener, BaseResponse.NETWORK_ERROR, BaseResponse.NETWORK_ERR);
            return;
        }
        Observable<T> observable = doRequest(map, method, MVPUtils.getTType(listener));
        if (funcs != null) {
            for (Func1 func1 : funcs) {
                observable = observable.map(func1);
            }
        }
        DefaultTSubscriber<T> defaultTSubscriber = new DefaultTSubscriber<T>() {
            @Override
            public void onResponse(T data) {
                caches.remove(map.getPath());
                if (listener != null) {
                    listener.onSuccess(data);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                caches.remove(map.getPath());
                if (listener != null) {
                    listener.onFail(code, msg);
                }
            }
        };
        caches.put(map.getPath(), defaultTSubscriber);
        hull(provider, observable).unsubscribeOn(Schedulers.io()).subscribe(defaultTSubscriber);
    }

    private <T> Observable<T> hull(Object provider, Observable<T> observable) {
        final Observable<T> hull;
        if (provider instanceof FragmentLifecycleProvider) {
            hull = RetrofitStringUtil.hull((FragmentLifecycleProvider) provider, observable);
        } else if (provider instanceof ActivityLifecycleProvider) {
            hull = RetrofitStringUtil.hull((ActivityLifecycleProvider) provider, observable);
        } else {
            hull = RetrofitStringUtil.hullString(observable);
        }
        return hull;
    }

    private <T> Observable<T> doRequest(RequestMap map, MVPUtils.Method method, final Type type) {
        Observable<Object> observable;
        String path = map.getPath();
        switch (method) {
            default:
            case GET:
                observable = createService().getTRequest(path, map);
                break;
            case POST:
                observable = createService().postTRequest(path, map);
                break;
            case FORM:
                observable = createService().formTRequest(path, map);
                break;
        }
        return observable.map(new Func1<Object, T>() {
            @Override
            public T call(Object obj) {
                if (obj != null) {
                    String data = GsonUtils.bean2Json(obj);
                    if (String.class.equals(type)) {
                        obj = data;
                    } else {
                        obj = GsonUtils.json2Bean(data, type);
                    }
                }
                return (T) obj;
            }
        });
    }

    void onFail(final OnLoadDataListener listener, final int errorCode, final String errorMsg) {
        UCardApplication.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFail(errorCode, errorMsg);
                }
            }
        }, 300);
    }

    /**
     * 下载指定文件
     *
     * @param url
     * @param listener
     */
    public static void downLoad(final String url, final OnDownloadListener listener) {
        RetrofitStringUtil.hullString(createDownLoadService().downLoad(url)).observeOn(Schedulers.io()).map(new Func1<ResponseBody, Boolean>() {
            @Override
            public Boolean call(ResponseBody body) {
                boolean result = true;
                try {
                    listener.map(FileUtils.getDownloadFile(url));
                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                }
                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                listener.onFail();
            }

            @Override
            public void onNext(Boolean b) {
                if (b) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }
            }
        });

    }

    /**
     * 创建服务类
     *
     * @return
     */
    protected NetworkService createService() {
        return createService(NetworkService.class);
    }

    private static NetworkService createDownLoadService() {
        checkDownLoadRetrofit();
        return downLoadRetrofit.create(NetworkService.class);
    }

    private static synchronized void checkDownLoadRetrofit() {
        if (downLoadRetrofit == null) {
            OkHttpClient okHttpClient = (new OkHttpClient.Builder()).connectTimeout(30L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).writeTimeout(30L, TimeUnit.SECONDS).addInterceptor(new DownloadInteceptor()).addInterceptor(new CommonParamInteceptor())
//                    .addInterceptor(new LogInteceptor())
                    .build();
            downLoadRetrofit = new Retrofit.Builder().baseUrl(NetworkAddress.BASE_URL).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
    }

    protected void cancel(String path) {
        Subscription subscription = caches.get(path);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    protected boolean processErrCode(int code, String msg, Object provider) {
        if (ERR_CODE_SYS_MAINTENANCE == code) {
            //系统维护中
            Context context = BaseApplication.getContext();
            EventBusUtils.post("_finish_self_");
            MaintenanceActivity.startActivity(context, msg);
            return true;
        } else if (ERR_CODE_USER_LOGOUT == code) {
            long cur = System.currentTimeMillis();
            if (cur - lastStart > 300) {
                lastStart = cur;
                Context context = BaseApplication.getContext();
                MainActivity.start(context);
                UCardUtil.startActivity(context, new Intent(context, LoginActivity.class));
            }
        }
        return false;
    }

    /**
     * 创建服务类
     *
     * @param cls
     * @param <T>
     * @return
     */
    protected <T> T createService(Class<T> cls) {
        return RetrofitUtil.createService(cls);
    }
}
