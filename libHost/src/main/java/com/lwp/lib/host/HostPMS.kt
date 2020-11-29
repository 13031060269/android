package com.lwp.lib.host

import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object HostPMS : InvocationHandler {
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
                "getServiceInfo",
                "getActivityInfo" -> {
                    val componentName = args?.get(0) as ComponentName
                    findApkInfo(componentName.packageName)?.apply {
                        return HostManager.findActivityInfo(componentName)
                    }
                    if (componentName.className == pluginActivity) {
                        return hostActivityInfo
                    }
                }
                "getPackageInfo", "getPackageInfoAsUser" -> {
                    val arg0 = args?.get(0)
                    if (arg0 is String) {
                        findApkInfo(arg0)?.apply {
                            return packageInfo
                        }
                    }
                }
                "getApplicationInfoAsUser", "getApplicationInfo" -> {
                    val arg0 = args?.get(0)
                    if (arg0 is String) {
                        findApkInfo(arg0)?.apply {
                            return mApplicationInfo
                        }
                    }
                }
                "getResourcesForApplicationAsUser",
                "getResourcesForApplication" -> {
                    var arg0 = args?.get(0)
                    if (arg0 is ApplicationInfo) {
                        arg0 = arg0.packageName
                    }
                    findApkInfo(arg0 as String)?.apply {
                        return mResources
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