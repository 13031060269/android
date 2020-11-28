package com.hfax.ucard.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.hfax.lib.BaseApplication;

import java.util.List;

/**
 * Created by eson on 2017/8/25.
 */

public class PermissionUtils {
    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_CONTACT_ERMISSION = 10999;

    /**
     * 初始化权限
     */
    public static void initPermission(Activity activity) {
        if (activity != null) {
            initSDCardPhoneStatePermission(activity);
            initLocationPermission(activity);
        }
    }

    /**
     * 判断一组权限是否已经被授权
     */
    public static boolean isPermissionGranted(List<String> permissions) {
        for (String permission : permissions) {
            if (PermissionChecker.checkSelfPermission(BaseApplication.getContext(), permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化权限,SD卡和联系人、手机IMEI权限请求
     */
    public static void initSDCardPhoneStatePermission(Activity activity) {
        if (activity != null) {
            PackageManager pkgManager = activity.getPackageManager();
            // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
            boolean sdCardWritePermission =
                    pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , activity.getPackageName()) == PackageManager.PERMISSION_GRANTED;

            // read phone state用于获取 imei 设备信息
            boolean phoneSatePermission =
                    pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE
                            , activity.getPackageName()) == PackageManager.PERMISSION_GRANTED;

            if (Build.VERSION.SDK_INT >= 23 && (!sdCardWritePermission || !phoneSatePermission)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION);
            }
        }
    }

    /**
     * 初始化权限,地理位置权限请求
     */
    public static void initLocationPermission(Activity activity) {
        if (activity != null) {
            PackageManager pkgManager = activity.getPackageManager();
            //获取用户地理位置权限
            boolean coarseLocation = pkgManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION
                    , activity.getPackageName()) == PackageManager.PERMISSION_GRANTED;
            boolean fineLocation = pkgManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION
                    , activity.getPackageName()) == PackageManager.PERMISSION_GRANTED;

            if (Build.VERSION.SDK_INT >= 23 && (!coarseLocation || !fineLocation)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
            }
        }
    }

    /**
     * 初始化权限，读取联系人权限
     *
     * @param activity
     */
    public static void initContactPermission(Activity activity) {
        if (activity != null) {
            PackageManager pkgManager = activity.getPackageManager();
            boolean contactPermission = pkgManager.checkPermission(Manifest.permission.READ_CONTACTS, activity.getPackageName())
                    == PackageManager.PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT >= 23 && !contactPermission) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CONTACT_ERMISSION);
            }
        }

    }

    /**
     * 开启设置页面
     *
     * @param context 上下文
     */
    public static void startSetting(Context context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


}
