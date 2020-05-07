package com.lwp.lib.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.lwp.lib.utils.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LwpNetwork {
    val service: NetworkService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(NetworkService::class.java)
    }

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
                if (responseBody.code == SUCCESS) {
                    val fromJson = fromJson<T>(responseBody.data)
                    onUi {
                        success(fromJson)
                    }
                } else {
                    onUi {
                        error(responseBody.code, responseBody.msg)
                    }
                }
            }
        } catch (e: Exception) {
            onUi {
                error(-1, "网络异常")
            }
        }
    }
}