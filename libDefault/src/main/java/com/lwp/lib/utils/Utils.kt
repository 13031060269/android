package com.lwp.lib.utils

import com.lwp.lib.database.Cache
import com.lwp.lib.database.cacheDao
import java.lang.reflect.Field

@Suppress("UNCHECKED_CAST")
fun <T> cast(obj: Any?): T {
    return obj as T
}

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