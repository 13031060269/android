package com.lwp.lib.utils

import com.lwp.lib.database.Cache
import com.lwp.lib.database.cacheDao
import com.lwp.lib.mvp.BaseModel
import java.lang.reflect.ParameterizedType

fun <T : BaseModel> getGenericType(obj: Any): Class<T> {
    val childClazz: Class<*> = obj.javaClass //子类字节码对象
    val genericSuperclass =
        childClazz.genericSuperclass as ParameterizedType?
    return cast(genericSuperclass!!.actualTypeArguments[0])
}

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