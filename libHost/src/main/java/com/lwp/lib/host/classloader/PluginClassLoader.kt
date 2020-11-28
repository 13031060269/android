package com.lwp.lib.host.classloader

import android.content.pm.ActivityInfo
import com.lwp.lib.host.*
import dalvik.system.DexClassLoader
import java.util.*

internal abstract class PluginClassLoader(
    dexPath: String?,
    private val optimizedDirectory: String?,
    private val librarySearchPath: String?,
    parent:ClassLoader?
) :
    DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {
    var stack: Stack<ActivityInfo> = Stack()
    private val activityMap = HashMap<String, ActivityLoader>()
    abstract fun createActivityDex(activityName: String): String

    @kotlin.jvm.Throws(ClassNotFoundException::class)
    public override fun loadClass(className: String, resolve: Boolean): Class<*>? {
        var result: Class<*>? = viewMap[className]
        if (className == pluginActivity) {
            val superClass = stack.peek().name
            var actLoader = activityMap[superClass]
            if (actLoader == null) {
                actLoader = ActivityLoader(createActivityDex(superClass))
                activityMap[superClass] = actLoader
            }
            result = actLoader.loadClass(className, resolve)
        }
        if (result == null) {
            result = findLoadedClass(className)
        }
        if (result == null) {
            try {
                result = findClass(className)
                if (resolve) {
                    resolveClass(result)
                }
            } catch (e: Exception) {
            }
        }
        if (result == null) {
            try {
                result = frameworkClassLoader.loadClass(className, resolve, true)
            } catch (e: Exception) {
            }
        }
        if (result == null) {
            printLog("loader$className 失败")
            throw ClassNotFoundException(className)
        }
        if (view.isAssignableFrom(result)) {
            viewMap[className] = result.asSubclass(view)
        }
        return result
    }

    inner class ActivityLoader(dexPath: String) :
        DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, this) {
        public override fun loadClass(name: String, resolve: Boolean): Class<*>? {
            if (pluginActivity == name) {
                var c = findLoadedClass(name)
                try {
                    if (c == null) {
                        c = findClass(name)
                    }
                    if (resolve) {
                        resolveClass(c)
                    }
                    return c
                } catch (e: Exception) {
                }
            }
            return this@PluginClassLoader.loadClass(name, resolve)
        }
    }
}

