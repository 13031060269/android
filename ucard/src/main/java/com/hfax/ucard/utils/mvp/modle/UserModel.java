package com.hfax.ucard.utils.mvp.modle;

import android.text.TextUtils;

import com.hfax.lib.AppConfig;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.network.Token;
import com.hfax.lib.network.cookie.OkHttpCookieManager;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eson on 2017/7/24.
 */

public class UserModel {

    private static String PWD_KEY;

    /**
     * 判断用户是否已经登录
     *
     * @return
     */
    public static boolean isLogin() {
        return !TextUtils.isEmpty(Token.getToken());
    }

    /**
     * 登出
     */
    public static void logout() {
        if (isLogin()) {
            //登出接口调用
            new SimplePresent().request(new RequestMap(NetworkAddress.LOGIN_OUT), null, MVPUtils.Method.GET, null);
            try {
                OkHttpCookieManager.clearCookies();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginBean.clear();
            if (AppConfig.IMUtil != null) {
                AppConfig.IMUtil.logoutChat();
            }
        }
    }

    public static String encodePwd(String pwd) {
        InputStream suffix = null;
        try {
            if (UCardUtil.isEmpty(PWD_KEY)) {
                suffix = BaseApplication.getContext().getAssets().open("suffix");
                byte[] bytes = new byte[suffix.available()];
                suffix.read(bytes);
                PWD_KEY = UCardUtil.binStrToStr(new String(bytes));
            }
        } catch (IOException e) {
            Utils.closeCloseable(suffix);
        }
        return Utils.md5Encode(pwd + PWD_KEY);
    }
}
