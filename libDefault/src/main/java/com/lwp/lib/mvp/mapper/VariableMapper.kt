package com.lwp.lib.mvp.mapper

import com.lwp.lib.mvp.interfaces.Factory

interface VariableMapper<T> {
    fun attachToDataBinding(container: T, factory: Factory)
}