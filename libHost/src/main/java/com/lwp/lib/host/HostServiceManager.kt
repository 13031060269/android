package com.lwp.lib.host

import android.content.ComponentName
import android.content.Context
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

class HostServiceManager : InvocationHandler {
    var mPMS: Any? = null
    fun hookPMS(context: Context) {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager")
            sPackageManagerField.isAccessible = true
            mPMS = sPackageManagerField[null]
            val iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager")
            val proxy = Proxy.newProxyInstance(
                iPackageManagerInterface.classLoader, arrayOf(iPackageManagerInterface),
                this
            )
            sPackageManagerField[null] = proxy
            val pm = context.packageManager
            val mPmField = pm.javaClass.getDeclaredField("mPM")
            mPmField.isAccessible = true
            mPmField[pm] = proxy
        } catch (ignored: Exception) {
        }
    }

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        try {
            when (method.name) {
                "getActivityInfo" -> {
                    val componentName = args?.get(0) as ComponentName
                    findApkInfo(componentName.packageName)?.apply {
                        return HostManager.findActivityInfo(componentName)
                    }
                }
            }
        } catch (e: Exception) {
        }
        return if (args == null) {
            method.invoke(mPMS)
        } else {
            method.invoke(mPMS, *args)
        }
    }
}