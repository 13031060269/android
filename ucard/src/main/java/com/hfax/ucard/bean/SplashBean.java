package com.hfax.ucard.bean;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.utils.PermissionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by eson on 2017/8/11.
 */

public class SplashBean implements Serializable {
    private static final long serialVersionUID = 5906157805936959948L;

    private static final String SPLASH_FILE = "_splash_file_";
    private static final int DEFAULT_SECOND = 5;

    public String url;//
    public String img;
    public String id;
    public String start;
    public String end;
    public String type;
    public String second;
    public String splash_1;
    public String splash_2;
    public String splash_3;

    /**
     * 广告是否有效
     *
     * @return
     */
    public boolean isValid() {
        if (TextUtils.isEmpty(img)) {
            return false;
        }
        long curTime = System.currentTimeMillis();
        long s = 0, e = 0;
        try {
            s = Long.valueOf(start);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            e = Long.valueOf(end);
        } catch (Exception ex) {
            e = 0;
            ex.printStackTrace();
            e = 0;
        }
        return (curTime >= s) && (e <= 0 || curTime < e);
    }

    public int getSecond() {
        int visibleSecond = DEFAULT_SECOND;
        try {
            visibleSecond = Integer.valueOf(second);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (visibleSecond <= 0) {
            visibleSecond = DEFAULT_SECOND;
        }
        return visibleSecond;
    }

    public static void save(SplashBean bean) {
        final Context context = BaseApplication.getContext();
        if (context != null && bean != null) {
            Utils.saveObjToFile(context, SPLASH_FILE, bean);
            if (bean.isValid() && !TextUtils.isEmpty(bean.img) && Utils.isWifi(context)) {
                Glide.with(context).load(bean.img).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }
        }
    }

    public static SplashBean get() {
        SplashBean bean = null;
        Context context = BaseApplication.getContext();
        if (context != null) {
            Object obj = Utils.getObjFromFile(context, SPLASH_FILE);
            if (obj != null && obj instanceof SplashBean) {
                bean = (SplashBean) obj;
            }
        }
        return bean;
    }

}
