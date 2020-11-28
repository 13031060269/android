package com.hfax.ucard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdk;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;
import com.google.gson.reflect.TypeToken;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.GsonUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiitHelper implements IIdentifierListener {
    private static final String MIIT_KEY = "miit_key";
    private volatile Map<String, String> data = new ConcurrentHashMap<>();
    private static MiitHelper INSTANCE = new MiitHelper();

    private MiitHelper() {
    }

    public static MiitHelper getINSTANCE() {
        return INSTANCE;
    }

    public void getDeviceIds(Context cxt) {
        long timeb = System.currentTimeMillis();
        int nres = CallFromReflect(cxt);
//        int nres=DirectCall(cxt);
        long timee = System.currentTimeMillis();
        long offset = timee - timeb;
        if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {//不支持的设备

        } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {//加载配置文件出错

        } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {//不支持的设备厂商

        } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程

        } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {//反射调用出错

        }
        Log.d(getClass().getSimpleName(), "return value: " + String.valueOf(nres));

    }

    public synchronized Map<String, String> getData() {
        if (data.isEmpty()) {
            try {
                String miit = getSp().getString(MIIT_KEY, null);
                if (TextUtils.isEmpty(miit)) {
                    getDeviceIds(BaseApplication.getContext());
                } else {
                    Map<String, String> map = GsonUtils.json2Bean(miit, new TypeToken<Map<String, String>>() {
                    }.getType());
                    if (map == null || map.isEmpty()) {
                        getDeviceIds(BaseApplication.getContext());
                    } else {
                        data.putAll(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    SharedPreferences getSp() {
        return BaseApplication.getContext().getSharedPreferences("sp_miit", Context.MODE_PRIVATE);
    }

    /*
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     *
     * */
    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

    /*
     * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
     * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
     * A和B不能存在直接和间接依赖关系，否则也会报错
     *
     * */
    private int DirectCall(Context cxt) {
        MdidSdk sdk = new MdidSdk();
        return sdk.InitSdk(cxt, this);
    }

    @Override
    public synchronized void OnSupport(boolean isSupport, IdSupplier _supplier) {
        if (_supplier == null ||!data.isEmpty()) {
            return;
        }
        Map<String, String> d = new HashMap<>();
        d.put("isSupport", String.valueOf(isSupport));
        d.put("oaid", _supplier.getOAID());
        d.put("vaid", _supplier.getVAID());
        d.put("aaid", _supplier.getAAID());
        d.put("udid", _supplier.getUDID());
        getSp().edit().putString(MIIT_KEY, GsonUtils.bean2Json(d)).apply();
        _supplier.shutDown();
        data.putAll(d);
    }

    public interface AppIdsUpdater {
        void OnIdsAvalid(@NonNull String ids);
    }

}
