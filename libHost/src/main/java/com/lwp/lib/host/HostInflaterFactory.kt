package com.lwp.lib.host

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory
import android.view.View
import android.view.ViewStub
import java.lang.reflect.Constructor
import java.util.*

internal class HostInflaterFactory : Factory {
    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val viewCaches = HashMap<String, Constructor<out View>>()
    private val mConstructor = arrayOf(
        Context::class.java, AttributeSet::class.java
    )

    override fun onCreateView(name: String, context: Context?, attrs: AttributeSet?): View? {
        if (-1 != name.indexOf('.')) {
            try {
                var constructor = viewCaches[name]
                if (constructor == null) {
                    val clazz =
                        frameworkClassLoader.loadClass(name).asSubclass(View::class.java)
                    constructor = clazz.getConstructor(*mConstructor)
                    constructor.isAccessible = true
                    viewCaches[name] = constructor
                }
                mConstructorArgs[0] = context
                mConstructorArgs[1] = attrs
                val args = mConstructorArgs
                return constructor?.newInstance(*args)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}

