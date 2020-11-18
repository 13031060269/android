package com.lwp.lib.network

class LwpResponseBody<T> {
    var data: T? = null
    var errCode: Int = 0
    var errMsg: String? = null
}