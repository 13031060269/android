package com.lwp.lib.network

import com.lwp.lib.mvp.HttpException
import com.lwp.lib.utils.*

suspend inline fun <reified T> loadData(
    requestBody: LwpRequestBody
): T {
    return try {
        onIo {
            val responseBody = request(requestBody)
            if (T::class.java == String::class.java) {
                cast<T>(responseBody)
            } else {
                fromJson(responseBody)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw HttpException(e.message, ERROR_NETWORK)
    }
}

suspend fun request(requestBody: LwpRequestBody): String {
    return when (requestBody.method) {
        POST -> {
            service.postAsync(requestBody.path, requestBody.data)
        }
        FORM -> {
            service.formAsync(requestBody.path, requestBody.data)
        }
        else -> {
            service.getAsync(requestBody.path, requestBody.data)
        }
    }.await()
}