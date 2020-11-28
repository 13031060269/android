package com.hfax.ucard.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.GsonUtils;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 要做缓存的bean继承本父类
 */
public class CacheBean {
    private static SharedPreferences sp;
    private static SharedPreferences spUserDiff;
    private static Map<String, CacheBean> caches = new WeakHashMap<>();
    private static Map<String, CacheBean> cachesUserDiff = new WeakHashMap<>();

    public void saveCache() {
        SharedPreferences sharedPreferences;
        String key = getClass().getName();
        Map<String, CacheBean> cachesMap;
        if (isUserDiff()) {
            cachesMap = cachesUserDiff;
            sharedPreferences = getUserDiffSharedPreference();
        } else {
            cachesMap = caches;
            sharedPreferences = getSharedPreference();
        }
        cachesMap.put(key, this);
        sharedPreferences
                .edit()
                .putString(key, GsonUtils.bean2Json(this))
                .apply();
    }

    /**
     * 是否根据不同的用户保存
     *
     * @return
     */
    protected boolean isUserDiff() {
        return true;
    }

    public static <T extends CacheBean> void clear(Class<T> clazz) {
        String key = clazz.getName();
        getUserDiffSharedPreference().edit().remove(key).apply();
        cachesUserDiff.remove(key);
        getSharedPreference().edit().remove(key).apply();
        caches.remove(key);
    }

    public static void clearUserDiff() {
        getUserDiffSharedPreference().edit().clear().apply();
        cachesUserDiff.clear();
    }


    public static <T extends CacheBean> T getCache(Class<T> clazz) {
        String key = clazz.getName();
        T cache;
        cache = (T) cachesUserDiff.get(key);
        if (cache == null) {
            cache = (T) caches.get(key);
        }
        String str;
        if (cache == null) {
            str = getUserDiffSharedPreference().getString(key, null);
            cache = GsonUtils.json2Bean(str, clazz);
            if (cache != null) {
                cachesUserDiff.put(key, cache);
            }
        }
        if (cache == null) {
            str = getSharedPreference().getString(key, null);
            cache = GsonUtils.json2Bean(str, clazz);
            if (cache != null) {
                caches.put(key, cache);
            }
        }
        return cache;
    }

    private static SharedPreferences getSharedPreference() {
        if (sp == null) {
            sp = BaseApplication.getContext().getSharedPreferences("javaBeanCaches", Context.MODE_PRIVATE);
        }
        return sp;
    }

    private static synchronized SharedPreferences getUserDiffSharedPreference() {
        if (spUserDiff == null) {
            spUserDiff = BaseApplication.getContext().getSharedPreferences("javaBeanCachesUserDiff", Context.MODE_PRIVATE);
        }
        return spUserDiff;
    }
}
