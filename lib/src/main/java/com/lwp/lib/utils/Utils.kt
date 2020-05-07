package com.lwp.lib.utils

import java.lang.reflect.ParameterizedType

fun <T> getGenericType(obj: Any): Class<T> {
    val childClazz: Class<*> = obj.javaClass //子类字节码对象
    val genericSuperclass =
        childClazz.genericSuperclass as ParameterizedType?
    return genericSuperclass!!.actualTypeArguments[0] as Class<T>
}