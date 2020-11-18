package com.lwp.lib.plugin

import com.lwp.lib.plugin.PluginManager
import kotlin.jvm.Throws

internal class FrameworkClassLoader(private val baseClassLoader: ClassLoader) :
    ClassLoader(baseClassLoader) {
    companion object {
        var plugId: String? = null
    }

    private var actName: String? = null
    fun newActivityClassName(id: String?, actName: String?): String {
        plugId = id
        this.actName = actName
        return ActivityOverrider.targetClassName
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        PluginManager.getInstance().getPluginById(plugId)?.apply {
            try {
                return if (className == ActivityOverrider.targetClassName && classLoader is PluginClassLoader) {
                    val actClassName = actName
                    (classLoader as PluginClassLoader).loadActivityClass(
                        actClassName!!
                    )
                } else {
                    classLoader.loadClass(className)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return super.loadClass(className, resolve)
    }
}