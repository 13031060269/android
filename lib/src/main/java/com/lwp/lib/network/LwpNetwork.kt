package com.lwp.lib.network

import com.lwp.lib.utils.*

open class LwpNetwork {
    suspend inline fun <reified T> request(
        requestBody: LwpRequestBody,
        crossinline success: (T) -> Unit,
        crossinline error: (Int, String?) -> Unit
    ) {
        try {
            onIo {
                val responseBody = kotlin.run {
                    when (requestBody.method) {
                        POST -> {
                            service.postAsync(requestBody.path, requestBody.data)
                        }
                        FORM -> {
                            service.formAsync(requestBody.path, requestBody.data)
                        }
                        else -> {
                            service.getAsync(requestBody.path, requestBody.data)
                        }
                    }
                }.await()
                if (responseBody.errCode == SUCCESS) {
                    val fromJson = fromJson<T>(responseBody.data)
                    onUi {
                        success(fromJson)
                    }
                } else {
                    onUi {
                        error(responseBody.errCode, responseBody.errMsg)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onUi {
                error(-1, e.message)
            }
        }
    }
}