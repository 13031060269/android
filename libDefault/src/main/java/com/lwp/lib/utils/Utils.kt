package com.lwp.lib.utils

import com.lwp.lib.database.Cache
import com.lwp.lib.database.cacheDao
import java.lang.RuntimeException
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST")
fun <T> cast(obj: Any?): T {
    return obj as T
}

inline fun <R, reified T : Any> T.generality(): R {
    var clazz: Class<*>? = this::class.java
    val stack = Stack<Class<*>>()
    while (clazz != null && clazz != T::class.java) {
        stack.push(clazz)
        clazz = clazz.superclass
    }
    while (stack.size > 0) {
        val genericSuperclass = stack.pop().genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            val arguments = genericSuperclass.actualTypeArguments
            if (!arguments.isNullOrEmpty()) {
                val type = arguments.last()
                if (type is Class<*>) {
                    return cast(type.newInstance())
                }
            }
        }
    }
    throw RuntimeException("没有找到泛型的实现！！,请把指定的类型放在最后面！！")
}

val <T> T.caches: HashMap<String, Any?>
    get() = throw RuntimeException("找不到属性caches！！")

inline fun <reified T> getCache(): T? {
    cacheDao.findData(T::class.java.name)?.apply {
        return fromJson<T>(json)
    }
    return null
}

inline fun <reified T> saveCache(t: T): Boolean {
    cacheDao.apply {
        findData(T::class.java.name)?.apply {
            json = toJson(t)
            update(this)
            return true
        }
        Cache(T::class.java.name, toJson(t)).apply {
            insert(this)
            return true
        }
    }
}

fun findField(
    targetClass: Class<*>,
    fieldName: String,
): Field? {
    var rsField: Field? = null
    try {
        rsField = targetClass.getDeclaredField(fieldName)
    } catch (e: NoSuchFieldException) {
    }
    if (rsField == null) {
        rsField = targetClass.superclass?.run { findField(this, fieldName) }
    }
    return rsField
}