package com.hfax.ucard.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by eson on 2017/8/23.
 * 监听每个activity的生命周期
 */

public class ActivityLifecycleCallbacksUtils implements Application.ActivityLifecycleCallbacks {
    public int mFinalCount;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mFinalCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--mFinalCount == 0) {//退到后台
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
