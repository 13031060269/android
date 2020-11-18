package com.lwp.lib.plugin

import dalvik.system.DexClassLoader
import java.util.*
import kotlin.jvm.Throws

internal class PluginClassLoader(
    dexPath: String?,
    optimizedDir: String,
    parent: ClassLoader?,
    private val thisPlugin: PluginInfo
) : DexClassLoader(
    dexPath,
    optimizedDir,
    thisPlugin.packageInfo?.applicationInfo?.nativeLibraryDir,
    parent
) {
    private val optimizedDirectory: String?
    private val libraryPath: String?
    private val proxyActivityLoaderMap: MutableMap<String, ClassLoader>

    init {
        proxyActivityLoaderMap = HashMap(thisPlugin.activities.size)
        libraryPath = thisPlugin.packageInfo.applicationInfo.nativeLibraryDir
        optimizedDirectory = optimizedDir
    }

    @Throws(ClassNotFoundException::class)
    fun loadActivityClass(actClassName: String): Class<*> {
        val dexSavePath = ActivityOverrider.createProxyDex(thisPlugin, actClassName, true)
        var actLoader = proxyActivityLoaderMap[actClassName]
        if (actLoader == null) {
            actLoader =
                ActivityClassLoader(dexSavePath.absolutePath, optimizedDirectory, libraryPath, this)
            proxyActivityLoaderMap[actClassName] = actLoader
        }
        return actLoader.loadClass(ActivityOverrider.targetClassName)
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        var c = findLoadedClass(name)
        if (c == null) {
            try {
                c = findClass(name)
            } catch (ignored: ClassNotFoundException) {
            }
            if (c == null) {
                c = parent.loadClass(name)
            }
        }
        if (resolve) {
            resolveClass(c)
        }
        return c
    }
}

internal class ActivityClassLoader(
    dexPath: String,
    optimizedDirectory: String?,
    librarySearchPath: String?,
    parent: ClassLoader
) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {
    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        if (ActivityOverrider.targetClassName == name) {
            var c = findLoadedClass(name)
            if (c == null) {
                c = findClass(name)
            }
            if (resolve) {
                resolveClass(c)
            }
            return c
        }
        return parent.loadClass(name)
    }

}