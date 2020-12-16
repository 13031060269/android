package com.lwp.lib.host

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class HostService : Service() {
    override fun onCreate() {
        super.onCreate()
        HostManager.init(application)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return HostBinder()
    }

    inner class HostBinder : Binder() {
        fun getService(): HostService {
            return this@HostService
        }
    }
}