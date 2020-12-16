package com.lwp.lib.host.hook

import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import com.lwp.lib.host.HostManager
import com.lwp.lib.host.findApkInfo
import com.lwp.lib.host.hostActivityInfo
import com.lwp.lib.host.pluginActivity
import com.lwp.lib.host.utils.HostUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object HookPMS : InvocationHandler {
    var mPMS: Any? = null
    fun hookPMS(context: Context) {
        try {
            val hookAbout = hookAbout("android.app.ActivityThread")
            val sPackageManager = hookAbout.getField<Any>("sPackageManager")
            mPMS = sPackageManager
            val iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager")
            val proxy = Proxy.newProxyInstance(
                iPackageManagerInterface.classLoader, arrayOf(iPackageManagerInterface),
                this
            )
            hookAbout.setField("sPackageManager", proxy)
            HostUtils.setFieldValue(context.packageManager, "mPM", proxy, true)
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