package com.hfax.ucard.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hfax.app.utils.ToastUtils;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.GsonUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.mvp.DataChange;
import com.qipeng.capatcha.QPCapatcha;
import com.qipeng.capatcha.QPCaptchaConfig;
import com.qipeng.capatcha.QPCaptchaListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class YunPianUtils {
    public static YunPian getYunPian() {
        return YunPian.yunPian;
    }

    private static String key;
    public static final String SCENE_LOGIN="1";
    public static final String SCENE_FORGET="2";

    public static class YunPian {

        private static String NORMAL = "获取验证码";
        private static YunPian yunPian = new YunPian();
        volatile WeakReference<TextView> tv = new WeakReference<>(null);
        Map<String, String> data = new HashMap<>();
        String text = NORMAL;
        boolean enable = true;
        Handler handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("DefaultLocale")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    setEnabled(false, String.format("%d秒后重发", msg.what));
                    handler.sendEmptyMessageDelayed(--msg.what, 1000);
                } else {
                    setEnabled(true);
                }
            }
        };

        public void requestYunPian(final Activity activity, final DataChange<Map<String, String>> dataChange) {
            requestYunPian(activity,key,dataChange);
        }
        public void requestYunPian(final Activity activity ,String scene_key, final DataChange<Map<String, String>> dataChange) {
            if(!TextUtils.equals(key,scene_key)){
                clear();
            }
            key=scene_key;
            if (data.isEmpty()) {
                JSONObject langPackModel = new JSONObject();
                try {
                    langPackModel.put("YPcaptcha_02", "请按顺序点击:");
                    langPackModel.put("YPcaptcha_03", "向右拖动滑块填充拼图");
                    langPackModel.put("YPcaptcha_04", "验证失败，请重试");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                QPCaptchaConfig config = new QPCaptchaConfig.Builder(activity).setAlpha(0.7f) // 视图透明度
                        .setWidth(Utils.getScreenWidth(BaseApplication.getContext())-Utils.dip2px(activity,60)) // 视图宽度
                        .setLangPackModel(langPackModel) // 界⾯面语⾔言配置
                        .showLoadingView(true) // 是否显示加载
                        .setLang(QPCaptchaConfig.LANG_ZH) // 语⾔言设置中⽂文或者英⽂文，默认中⽂文
                        .setCallback(new QPCaptchaListener() {
                            @Override
                            public void onLoaded() {

                            }

                            @Override
                            public void onSuccess(String s) {
                                if (!UCardUtil.isEmpty(s)) {
                                    GrowingIOUtils.trackSDA(UCardConstants.SLIDE_VERFI, new UCardConstants.RESULT());
                                    data = GsonUtils.json2Bean(s, new TypeToken<Map<String, String>>() {
                                    }.getType());
                                    dataChange.onChange(data);
                                } else {
                                    onError("返回了空数据");
                                }
                            }

                            @Override
                            public void onFail(String s) {
                                UCardConstants.RESULT result = new UCardConstants.RESULT();
                                result.result = UCardConstants.UCARD_SDA_FAILED;
                                result.error_type = s;
                                GrowingIOUtils.trackSDA(UCardConstants.SLIDE_VERFI, result);
//                                ToastUtils.getToast().showToast(s);
                                dataChange.onChange(null);
                            }

                            @Override
                            public void onError(String s) {
                                UCardConstants.RESULT result = new UCardConstants.RESULT();
                                result.result = UCardConstants.UCARD_SDA_FAILED;
                                result.error_type = s;
                                GrowingIOUtils.trackSDA(UCardConstants.SLIDE_VERFI, result);
//                                ToastUtils.getToast().showToast(s);
                                dataChange.onChange(null);
                            }

                            @Override
                            public void onCancel() {
                                UCardConstants.RESULT result = new UCardConstants.RESULT();
                                result.result = UCardConstants.UCARD_SDA_FAILED;
                                result.error_type = "取消";
                                GrowingIOUtils.trackSDA(UCardConstants.SLIDE_VERFI, result);
                                dataChange.onChange(null);
                            }
                        }) // 设置回调接⼝口
                        .build();
                QPCapatcha.getInstance().verify(config);
            } else {
                dataChange.onChange(data);
            }
        }

        public void setTV(TextView tv) {
            this.tv = new WeakReference<>(tv);
            setEnabled(enable, text);
        }

        public void setEnabled(boolean enabled) {
            setEnabled(enabled, NORMAL);
        }

        public void startCountDown() {
            setEnabled(false, text);
            handler.sendEmptyMessage(60);
        }

        void setEnabled(boolean enabled, String text) {
            TextView tv = YunPian.this.tv.get();
            this.enable = enabled;
            this.text = text;
            if (tv != null) {
                tv.setEnabled(enabled);
                tv.setText(text);
            }
        }

        public void clear() {
            data.clear();
        }
    }
}
