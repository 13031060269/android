package com.lwp.lib.network

import com.lwp.lib.utils.GET

const val netDelay = 500

open class LwpRequestBody(
    val path: String,
    val data: MutableMap<String, Any> = HashMap(),
    @Method val method: Int = GET,
    val minDelay: Int = 0
)

class LwpRequestBodyDelay(
    path: String,
    data: MutableMap<String, Any> = HashMap(),
    method: Int = GET,
) : LwpRequestBody(path, data, method, netDelay)

