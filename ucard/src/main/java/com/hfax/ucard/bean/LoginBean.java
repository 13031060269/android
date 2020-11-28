package com.hfax.ucard.bean;

import android.text.TextUtils;

import com.hfax.app.utils.EventBusUtils;
import com.hfax.lib.network.Token;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.modules.getui.GetuiAliasUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.YunPianUtils;

import java.io.Serializable;

/**
 * 登录bean
 *
 * @author SongGuangYao
 */

public class LoginBean extends CacheBean implements Serializable {

    private static final long serialVersionUID = -4932346780868481205L;

    //用于的后续登录的authKey
    public String accessToken;
    /**
     * growingIO ID
     */
    public String usguid;
    /**
     * 用户手机号
     */
    public String mobile;
    /**
     * 用户ID
     */
    public String userid;


    public void save() {
        Token.saveToken(accessToken);
        saveCache();
    }

    public static void clear() {
        Token.clearToken();
        CacheBean.clearUserDiff();
        EventBusUtils.post(BaseNetworkActivity.ACTION_LOGOUT);
        GrowingIOUtils.setUserId();
        GetuiAliasUtils.updateGeTuiAlias();
        YunPianUtils.getYunPian().clear();
    }

    /**
     * 获取当前用户手机号
     *
     * @return
     */
    public static String getMobile() {
        LoginBean cache = getCache(LoginBean.class);
        if (cache != null) {
            return cache.mobile;
        }
        return null;
    }

    /**
     * 获取当前用户唯一标识
     *
     * @return
     */
    public static String getUId() {
        LoginBean cache = getCache(LoginBean.class);
        if (cache != null) {
            return cache.userid;
        }
        return null;
    }

    /**
     * 获取当前用户growingio ID
     *
     * @return
     */
    public static String getGOID() {
        LoginBean cache = getCache(LoginBean.class);
        if (cache != null) {
            return cache.usguid;
        }
        return null;
    }

    /**
     * 更新手机号
     *
     * @param mobile
     */
    public static void updateMobile(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            LoginBean cache = getCache(LoginBean.class);
            if (cache != null) {
                if (!mobile.equals(cache.mobile)) {
                    cache.mobile = mobile;
                    cache.save();
                }
            }
        }
    }
}
