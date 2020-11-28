package com.hfax.ucard.utils;

import com.google.gson.reflect.TypeToken;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.network.BaseResponse;
import com.hfax.lib.utils.GsonUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.bean.CacheBean;
import com.hfax.ucard.bean.GlobalConfigBean;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleNetworkAdapter;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

/**
 * Created by liuweiping on 2018/6/25.
 */

public class GlobalConfigUtils {
    public static GlobalConfigBean getGlobalConfig() {
        GlobalConfigBean cache = CacheBean.getCache(GlobalConfigBean.class);
        if (cache == null) {
            String globalConfig = Utils.readAssetsFile(BaseApplication.getContext(), "global.config");
            BaseResponse<GlobalConfigBean> baseResponse = GsonUtils.json2Bean(globalConfig, new TypeToken<BaseResponse<GlobalConfigBean>>() {
            }.getType());
            if (baseResponse != null) {
                cache = baseResponse.data;
                cache.saveCache();
            }
        }
        return cache;
    }

    public static void request(SimpleNetworkAdapter adapter) {
        RequestMap map = new RequestMap(NetworkAddress.CONFIG_DICT);
        GlobalConfigBean cache = GlobalConfigUtils.getGlobalConfig();
        if(cache!=null){
            map.put("lastUpdateTimeStamp",cache.lastUpdateTimeStamp);
        }
        adapter.request(map, MVPUtils.Method.GET, new SimpleViewImpl<GlobalConfigBean>() {
            @Override
            public void onSuccess(GlobalConfigBean bean) {
                bean.saveCache();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
