package com.lwp.lib.utils

import java.util.*
import kotlin.collections.HashMap

private val variables: WeakHashMap<Any, HashMap<Class<*>, Any>> by lazy { WeakHashMap() }
fun <T : Any, R : Any> T.saveVar(variable: R) {
    var vars = variables[this]
    if (vars == null) {
        vars = HashMap()
        variables[this] = vars
    }
    vars[variable::class.java] = variable
}

inline fun <T : Any, reified R : Any> T.lazyVar(noinline initializer: () -> R): R {
    return lazyVar(initializer, R::class.java)
}

fun <T : Any, R : Any> T.lazyVar(initializer: () -> R, clazz: Class<R>): R {
    return getVar(clazz) ?: lazy(initializer).value.also {
        saveVar(it)
    }
}

fun <T : Any, R : Any> T.getVar(clazz: Class<R>): R? = variables[this]?.run {
    cast(this[clazz])
}

inline fun <T : Any, reified R : Any> T.getVar(): R? = getVar(R::class.java)

fun <T : Any> T.clearVar() = variables.remove(this)?.clear()