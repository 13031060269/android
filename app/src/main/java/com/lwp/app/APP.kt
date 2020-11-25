package com.lwp.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.lwp.lib.host.HostManager

class APP : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        HostManager.init(this)
    }
}