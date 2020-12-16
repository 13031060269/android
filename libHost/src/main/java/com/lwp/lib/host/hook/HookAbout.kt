package com.lwp.lib.host.hook

import java.lang.Class.forName
import java.lang.reflect.Field

class HookAbout(val clazz: Class<*>, create: (about: HookAbout) -> Any) {
    private val fieldCache = HashMap<String, Field>()
    val hook: Any by lazy {
        create(this)
    }

    fun <F> getField(field: String, obj: Any? = hook): F {
        return field(field).get(obj) as F
    }

    private fun field(field: String): Field {
        return fieldCache[field] ?: clazz.getDeclaredField(field)
            .apply {
                isAccessible = true
                fieldCache[field] = this
            }
    }

    fun setField(field: String, obj: Any?) {
        return field(field).set(hook, obj)
    }
}

private val hookCache = HashMap<String, HookAbout>()
fun hookAbout(
    className: String,
    create: (about: HookAbout) -> Any = {
        it.clazz.newInstance()
    }
): HookAbout {
    return hookCache[className] ?: HookAbout(
        forName(className),
        create
    ).apply { hookCache[className] = this }
}
