package com.lwp.lib.network

import com.lwp.lib.utils.GET

class LwpRequestBody(val path: String, val data: MutableMap<String, Any>, @Method val method: Int= GET)
