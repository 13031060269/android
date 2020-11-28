package com.hfax.ucard.utils;

import com.hfax.lib.AppConfig;
import com.hfax.lib.BaseApplication;
import com.hfax.ucard.bean.LoginBean;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;


/**
 * Created by eson on 2018/2/26.
 */

public class IMUtils implements AppConfig.IMUtil{

    private static final String APP_KEY = "3e0ec330fb514bfa9219855833a3617c";

    /**
     * 初始化客服SDK
     */
    public  void initIM() {
        String uid = LoginBean.getGOID();
        if (uid == null) {
            uid = "";
        }
        SobotApi.initSobotSDK(BaseApplication.getContext(), APP_KEY, uid);
    }

    @Override
    public void initIM(String s) {

    }

    /**
     * 打开客服聊天界面
     */
    public  void startChat() {
        Information information = new Information();
        information.setAppkey(APP_KEY);
//        information.setUseRobotVoice(true);//机器人语音
        String uid = LoginBean.getGOID();
        if (uid != null) {
            information.setUid(uid);
        }
        SobotApi.startSobotChat(BaseApplication.getContext(), information);
    }

    /**
     * 退出聊天系统
     */
    public void logoutChat() {
        SobotApi.exitSobotChat(BaseApplication.getContext());
    }
}
