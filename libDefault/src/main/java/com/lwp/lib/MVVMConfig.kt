package com.lwp.lib

import android.app.Application

object MVVMConfig {
    internal lateinit var myApplication: Application
    fun init(application: Application) {
        myApplication = application
    }
}
