package com.lwp.lib.host

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory
import android.view.View
import android.view.ViewStub
import java.lang.reflect.Constructor
import java.util.*

internal class HostLayoutInflater : LayoutInflater {
    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val viewCaches = HashMap<String, Constructor<out View>>()
    override fun cloneInContext(newContext: Context): LayoutInflater =
        HostLayoutInflater(this, newContext)

    constructor(mContext: Context) : super(mContext) {
        factory = Factory { name: String, context: Context, attrs: AttributeSet? ->
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
                    val view = constructor!!.newInstance(*args)
                    if (view is ViewStub) {
                        view.layoutInflater = cloneInContext(context)
                    }
                    return@Factory view
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            null
        }
    }

    constructor(inflater: HostLayoutInflater, newContext: Context) : super(inflater, newContext)

    @Throws(ClassNotFoundException::class)
    override fun onCreateView(name: String, attrs: AttributeSet): View {
        for (prefix in sClassPrefixList) {
            try {
                val view = createView(name, prefix, attrs)
                if (view != null) {
                    return view
                }
            } catch (ignored: ClassNotFoundException) {
            }
        }
        return super.onCreateView(name, attrs)
    }

    companion object {
        val mConstructor = arrayOf(
            Context::class.java, AttributeSet::class.java
        )
        private val sClassPrefixList = arrayOf(
            "android.widget.",
            "android.webkit.",
            "android.app."
        )
    }
}