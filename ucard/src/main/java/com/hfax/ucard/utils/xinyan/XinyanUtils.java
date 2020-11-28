package com.hfax.ucard.utils.xinyan;

import android.content.Context;

import com.google.gson.Gson;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.bean.CollectBean;
import com.xinyan.android.device.sdk.XinyanDeviceSDK;
import com.xinyan.android.device.sdk.interfaces.OnDeviceListener;

/**
 * 获取Xinyan
 *
 * @author SongGuangYao
 * @date 2018/9/10
 */

public class XinyanUtils {

    private static String token;
    private static String sign;
    private static final String xinYanId = "8150708715";
    private static XinyanDeviceSDK mXinyanDeviceSDK;


    /**
     * 获取FMId
     *
     * @param context
     */
    public static void getXinyan(final Context context, final CallBack callBack) {
        //如果存在直接返回
        if (token != null && sign != null) {
            if (callBack != null) {
                callBack.callBack(token, sign);
            }
            return;
        }
        if (mXinyanDeviceSDK == null) {
            mXinyanDeviceSDK = XinyanDeviceSDK.getInstents().init(context.getApplicationContext());
            mXinyanDeviceSDK.isDebug(BuildConfig.DEBUG);
        }
        mXinyanDeviceSDK.setOnDeviceListener(new OnDeviceListener() {
            @Override
            public void callback(String result) {
                try {
                    CollectBean collectBean = new Gson().fromJson(result, CollectBean.class);
                    if (collectBean != null && collectBean.isSuccess()) {
                        token = collectBean.getResult().getToken();
                        sign = collectBean.getResult().getSign();
                        if (callBack != null) {
                            callBack.callBack(token, sign);
                        }
                    } else {
                        if (callBack != null) {
                            callBack.error(collectBean != null ? collectBean.getErrorMsg() : "返回值为空");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mXinyanDeviceSDK.execute(xinYanId);
    }


    /**
     * 释放资源
     */
    public static void destory() {
        if (mXinyanDeviceSDK != null) {
            mXinyanDeviceSDK.destory();
            mXinyanDeviceSDK = null;
        }
    }

    public interface CallBack {
        void callBack(String token, String sign);

        void error(String msg);
    }
}
