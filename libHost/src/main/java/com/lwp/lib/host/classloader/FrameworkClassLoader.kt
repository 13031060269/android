package com.lwp.lib.host.classloader

import com.lwp.lib.host.apkId
import com.lwp.lib.host.findApkInfo
import com.lwp.lib.host.utils.HostUtils
import dalvik.system.PathClassLoader

internal class FrameworkClassLoader(private val baseClassLoader: ClassLoader) :
    PathClassLoader("", baseClassLoader) {
    @kotlin.jvm.Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*>? {
        return loadClass(className, resolve, false)
    }

    @kotlin.jvm.Throws(ClassNotFoundException::class)
    fun loadClass(className: String, resolve: Boolean, notFound: Boolean): Class<*>? {
        var c: Class<*>? = null
        if (apkId == null || notFound) {
            try {
                c = baseClassLoader.loadClass(className)
            } catch (e: Exception) {
            }
        } else {
            c = findApkInfo(apkId)?.loadClass(className, resolve)
        }
        if (c == null) {
            throw ClassNotFoundException(className)
        }
        return c
    }
}
