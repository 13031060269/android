package com.lwp.lib.mvp

import com.lwp.lib.utils.cast

interface That<T> {
    val that
        get() = cast<T>(this)
}