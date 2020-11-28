package com.hfax.ucard.utils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.SPUtils;

public class SPManager extends SPUtils {

    public static final String SP_KEY_PROMISES = "sp.key.promises";//首页请求权限

    public static void putBoolean(String key, boolean defValue) {
         putBoolean(BaseApplication.getContext(), key, defValue);
    }
    public static boolean getBoolean(String key, boolean defValue) {
        return getBoolean(BaseApplication.getContext(), key, defValue);
    }

    public static void putString(String key, String value) {
        putString(BaseApplication.getContext(), key, value);
    }

    public static String getString(String key, String defValue) {
        return getString(BaseApplication.getContext(), key, defValue);
    }

    public static void putInt(String key, int value) {
        putInt(BaseApplication.getContext(), key, value);
    }

    public static int getInt(String key, int defValue) {
        return getInt(BaseApplication.getContext(), key, defValue);
    }

    public static void putLong(String key, long value) {
        putLong(BaseApplication.getContext(), key, value);
    }

    public static long getLong(String key, long defValue) {
        return getLong(BaseApplication.getContext(), key, defValue);
    }

    public static void remove(String key) {
        remove(BaseApplication.getContext(), key);
    }

}
