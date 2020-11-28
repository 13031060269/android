package com.hfax.ucard.utils;

import android.text.TextUtils;

import com.hfax.lib.utils.GsonUtils;
import com.hfax.ucard.bean.LoginBean;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

/**
 * Created by eson on 2017/8/23.
 */

public class GrowingIOUtils {
    /**
     * 发送事件
     *
     * @param eventName 事件名字
     */
    public static void track(String eventName) {
//        GrowingIO.getInstance().track(eventName);
    }

    /**
     * 发送事件级变量
     *
     * @param eventName       事件名字
     * @param eventProperties 事件数据的JSONObject
     */
    public static void track(String eventName, JSONObject eventProperties) {
//        GrowingIO.getInstance().track(eventName, eventProperties);
    }


    /**
     * 发送事件级变量
     *
     * @param eventName       事件名字
     * @param eventProperties 事件数据的JSONObject
     */
    public static void trackSDA(String eventName, Object eventProperties) {
        JSONObject jsonObject = null;
        if (eventProperties != null) {
            if (eventProperties instanceof JSONObject) {
                jsonObject = (JSONObject) eventProperties;
            } else {
                jsonObject = GsonUtils.toJSONObject(GsonUtils.bean2Json(eventProperties));
            }
        }
        SensorsDataAPI.sharedInstance().track(eventName, jsonObject);
    }

    /**
     * 发送事件级变量
     *
     * @param eventName 事件名字
     */
    public static void trackSDA(String eventName) {
        SensorsDataAPI.sharedInstance().track(eventName);
    }


    /**
     * 设置用户id
     */
    public static void setUserId() {
        String uId = LoginBean.getUId();
//        GrowingIO.getInstance().setUserId(uId);
        if (!TextUtils.isEmpty(uId)) {
            SensorsDataAPI.sharedInstance().login(uId);
        }
    }
}
