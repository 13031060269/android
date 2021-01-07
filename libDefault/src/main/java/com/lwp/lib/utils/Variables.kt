package com.lwp.lib.utils

import java.util.*
import kotlin.collections.HashMap

private val variables: WeakHashMap<Any, HashMap<Class<*>, Any>> by lazy { WeakHashMap() }
fun <T : Any, R : Any> T.saveVar(variable: R?) {
    if (variable == null) return
    var vars = variables[this]
    if (vars == null) {
        vars = HashMap()
        variables[this] = vars
    }
    vars[variable::class.java] = variable
}

fun <T : Any, R : Any> T.getVar(clazz: Class<R>): R? = variables[this]?.run {
    cast(getValueByClass(this, clazz))
}

inline fun <T : Any, reified R : Any> T.getVar(): R? = getVar(R::class.java)

fun <T : Any> T.clearVar() = variables.remove(this)?.clear()