package com.hfax.ucard.modules.debug;

import android.text.TextUtils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.network.RetrofitUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.utils.mvp.NetworkAddress;

public class DebugUtil {
    private static final String FILE_URL_LOCAL = "file.url.local";

    public static String getLocalUrl() {
        Object obj = Utils.getObjFromFile(BaseApplication.getContext(), FILE_URL_LOCAL);
        return (String) obj;
    }

    public static void saveLocalUrl(String url) {
        Utils.saveObjToFile(BaseApplication.getContext(), FILE_URL_LOCAL, url);
    }

    /**
     * 项目初始化时候debug的一些设置
     * @param application
     * @return
     */
    public static boolean init(BaseApplication application){
        if (BuildConfig.DEBUG) {

            //获取本地存储的上次选择环境
            String baseUrl = DebugUtil.getLocalUrl();
            if (!TextUtils.isEmpty(baseUrl)) {
                NetworkAddress.BASE_URL = baseUrl;
                RetrofitUtil.resetBaseUrl(baseUrl, baseUrl.contains("hfax.com"));
            }else{
                RetrofitUtil.init(NetworkAddress.BASE_URL);
            }
        }
        return BuildConfig.DEBUG;
    }
}
