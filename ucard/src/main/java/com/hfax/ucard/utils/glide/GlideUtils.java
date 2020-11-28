package com.hfax.ucard.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.hfax.lib.utils.LogUtil;

/**
 * Created by eson on 2017/8/9.
 */

public class GlideUtils {
    private static GlideUtils sGlideUtils;

    private GlideUtils() {
    }

    public static synchronized GlideUtils getInstance() {
        if (sGlideUtils == null) {
            synchronized (GlideUtils.class) {
                if (sGlideUtils == null) {
                    sGlideUtils = new GlideUtils();
                    return sGlideUtils;
                }
            }
        }
        return sGlideUtils;
    }


    /**
     * 获取带cookie的glide url
     *
     * @param url
     * @return
     */
    public static GlideUrl createUrlWithCookie(String url) {
        GlideUrl glideUrl = null;
        if (!TextUtils.isEmpty(url)) {
            glideUrl = new GlideUrl(url);
        }
        return glideUrl;
    }

    public static void requestImageCode(Activity activity, String url, ImageView imageView) {
        if (activity != null && !TextUtils.isEmpty(url) && imageView != null) {
            Glide.with(activity).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    LogUtil.e("Glide onException === 资源加载异常");
                    return false;
                }

                //这个用于监听图片是否加载完成
                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    LogUtil.e("Glide onException === 图片加载完成");
                    return false;
                }
            }).into(imageView);
        }
    }

    public static void requestImageCode(Context activity, String url, ImageView imageView) {
        if (activity != null && !TextUtils.isEmpty(url) && imageView != null) {
            Glide.with(activity).load(url).into(imageView);
        }
    }

    public void imageLoad(Activity activity, String url, ImageView imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isDestroyed()) {
                Glide.with(activity).load(url).into(imageView);
            }
        } else {
            Glide.with(activity).load(url).into(imageView);
        }
    }

    public void imageLoad(Activity activity, String url, GlideDrawableImageViewTarget imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isDestroyed()) {
                Glide.with(activity).load(url).into(imageView);
            }
        } else {
            Glide.with(activity).load(url).into(imageView);
        }
    }

    public void imageLoad(Fragment fragment, String url, ImageView imageView) {
        if (fragment != null && fragment.getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!fragment.getActivity().isDestroyed()) {
                    Glide.with(fragment.getActivity()).load(url).into(imageView);
                }
            } else {
                Glide.with(fragment.getActivity()).load(url).into(imageView);
            }

        }
    }

    public void imageLoad(Fragment fragment, String url, GlideDrawableImageViewTarget imageView) {
        if (fragment != null && fragment.getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!fragment.getActivity().isDestroyed()) {
                    Glide.with(fragment.getActivity()).load(url).into(imageView);
                }
            } else {
                Glide.with(fragment.getActivity()).load(url).into(imageView);
            }

        }
    }


}
