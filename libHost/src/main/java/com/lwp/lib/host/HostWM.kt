package com.lwp.lib.host

import android.app.Activity
import android.view.WindowManager
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

internal class HostWM(activity: Activity, private val apkInfo: ApkControl?) : InvocationHandler {
    val mWM: Any
    init {
        val windowManager = WindowManager::class.java
        val proxy = Proxy.newProxyInstance(
            windowManager.classLoader, arrayOf(windowManager),
            this
        )
        mWM = HostUtils.getFieldValue(activity, "mWindowManager", true)
        HostUtils.setFieldValue(activity, "mWindowManager", proxy, true)
    }

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        try {
            when (method.name) {
                "addView" -> {
                    val layoutParams = args?.get(1)
                    if (layoutParams is WindowManager.LayoutParams) {
                        layoutParams.packageName = hostComponentName.packageName
                        layoutParams.windowAnimations = apkInfo?.windowAnimations ?: 0
                    }
                }
            }
        } catch (e: Exception) {
        }
        return if (args == null) {
            method.invoke(mWM)
        } else {
            method.invoke(mWM, *args)
        }
    }
}