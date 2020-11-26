package com.lwp.lib.host

import dalvik.system.PathClassLoader

internal class FrameworkClassLoader(private val baseClassLoader: ClassLoader) :
    PathClassLoader("", null) {
    init {
        HostUtils.setFieldValue(
            baseClassLoader, "parent",
            null, true
        )
    }

    @kotlin.jvm.Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*>? {
        return loadClass(className, resolve, false)
    }

    @kotlin.jvm.Throws(ClassNotFoundException::class)
    fun loadClass(className: String, resolve: Boolean, notFound: Boolean): Class<*>? {
        var c: Class<*>? = null
        if (apkId == null || notFound) {
            try {
                return baseClassLoader.loadClass(className)
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

    private fun load(className: String, loader: ClassLoader?): Class<*>? {
        return try {
            loader?.loadClass(className)
        } catch (e: Exception) {
            load(className, loader?.parent)
        }
    }
}
