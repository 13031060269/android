package com.lwp.lib.mvp.interfaces

interface VoidCallback {
    fun callback()
}

interface ValueCallback<T> {
    fun callback(value: T)
}

interface ValueReturnCallback<T, R> {
    fun callback(value: T): R
}
