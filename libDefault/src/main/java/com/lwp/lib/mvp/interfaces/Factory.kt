package com.lwp.lib.mvp.interfaces

interface Factory {
    fun <C> create(clazz: Class<C>): C?
}