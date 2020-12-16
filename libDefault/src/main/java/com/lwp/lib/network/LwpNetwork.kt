package com.lwp.lib.network

import android.os.SystemClock
import com.lwp.lib.mvp.view_model.HttpException
import com.lwp.lib.utils.*
import kotlinx.coroutines.delay

suspend inline fun <reified T> loadData(
    requestBody: LwpRequestBody,
): T {
    val uptimeMillis = SystemClock.uptimeMillis()
    return try {
        val responseBody = request(requestBody)
        val delay = SystemClock.uptimeMillis() - uptimeMillis
        if (delay < requestBody.minDelay) {
            delay(requestBody.minDelay - delay)
        }
        if (T::class.java == String::class.java) {
            cast(responseBody)
        } else {
            fromJson(responseBody)
        }
    } catch (e: Exception) {
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