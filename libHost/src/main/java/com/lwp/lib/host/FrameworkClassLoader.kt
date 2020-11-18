package com.lwp.lib.host

import java.util.*
import kotlin.jvm.Throws

internal class FrameworkClassLoader(baseClassLoader: ClassLoader) :
    ClassLoader(baseClassLoader.parent) {
    private val classLoaders: MutableList<ClassLoader> by lazy { LinkedList() }
    init {
        classLoaders.add(baseClassLoader)
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        try {
            val last = classLoaders.last()
            val loadClass = last.loadClass(className)
            if (loadClass != null && resolve) {
                resolveClass(loadClass)
            }
            return loadClass
        } catch (e: Exception) {
        }
        return super.loadClass(className, resolve)
    }
}