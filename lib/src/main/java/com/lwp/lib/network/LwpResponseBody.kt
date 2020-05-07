package com.lwp.lib.network

class LwpResponseBody<T> {
    var data: T? = null
    var code: Int = 0
    var msg: String? = null
}