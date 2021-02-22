package com.lwp.lib.network

data class LwpRequestBody @JvmOverloads constructor(
    val path: String = "",
    val data: MutableMap<String, Any> = HashMap(),
    @Method val method: Int = GET,
    val minDelay: Int = 0
)
