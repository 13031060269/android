package com.lwp.lib.mvp.interfaces

import com.lwp.lib.mvp.view_model.LwpViewModel

interface Factory {
    fun <T> create(clazz: Class<T>): T?
}

inline fun <reified T : LwpViewModel<*>> Factory.create(): T? = create(T::class.java)