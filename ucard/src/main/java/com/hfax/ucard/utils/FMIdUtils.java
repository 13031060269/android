package com.hfax.ucard.utils;

import android.content.Context;

import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.modules.user.LoginByPwdActivity;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取同盾FMId
 *
 * @author SongGuangYao
 * @date 2018/9/10
 */

public class FMIdUtils {

    private static String fmId;

    public static void init(Context context) {
        if (BuildConfig.DEBUG) {//开发
            FMAgent.init(context.getApplicationContext(), FMAgent.ENV_SANDBOX);
        } else {//生产
            FMAgent.init(context.getApplicationContext(), FMAgent.ENV_PRODUCTION);
        }
    }

    /**
     * 获取FMId
     *
     * @param context
     */
    public static void getFMId(final Context context, final CallBack callBack) {
        //如果存在直接返回
        if (fmId != null) {
            if (callBack != null) {
                callBack.callBack(fmId);
            }
            return;
        }
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String s = FMAgent.onEvent(context.getApplicationContext());
                subscriber.onNext(s);
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callBack != null) {
                            callBack.callBack("");
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        fmId = s;
                        if (callBack != null) {
                            callBack.callBack(fmId);
                        }
                    }
                });
    }

    public static void getFMId(Context context) {
        getFMId(context, null);
    }

    public interface CallBack {
        void callBack(String fmId);

        void error(String msg);
    }
}
