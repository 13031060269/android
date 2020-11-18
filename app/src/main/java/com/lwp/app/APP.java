package com.lwp.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.lwp.lib.plugin.PluginManager;

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
//        PluginManager.init(this);
    }
}

