package com.hfax.ucard.utils;

import android.text.TextUtils;

import com.hfax.lib.utils.Utils;

/**
 * Created by 王静 on 2017/11/17.
 */

public class VersionUtils {
    private static String currentVersion = Utils.APP_VERSION;

    /**
     * 判断是大版本、小版本
     *
     * @param versionName
     * @return
     */
    public static boolean isBigVersion(String versionName) {
        if (TextUtils.isEmpty(versionName) || TextUtils.isEmpty(currentVersion)) {
            return false;
        }
        String[] newVersionData = (versionName.replace(".", "/")).split("/");
        String[] currentVersionData = (currentVersion.replace(".", "/")).split("/");
        try {
            for (int i = 0; i < newVersionData.length - 1; i++) {
                if (Integer.parseInt(newVersionData[i]) > Integer.parseInt(currentVersionData[i])) {
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}
