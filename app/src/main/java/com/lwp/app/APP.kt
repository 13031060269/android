package com.lwp.app

import android.app.Application
import android.content.Context
import com.lwp.lib.MVVMConfig
import com.lwp.lib.host.HostManager

class APP : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        HostManager.init(this)
        MVVMConfig.init(this)
    }
}