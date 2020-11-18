package com.lwp.lib.utils

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.lwp.lib.network.NetworkService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


const val GET = 0x0
const val POST = 0x1
const val FORM = 0x2
const val SUCCESS = GET
const val ERROR_NETWORK = -1


var BASE_URL = "https://m.beneucard.com/"
    set(value) {
        if (field != value) {
            field = value
            client.dispatcher.queuedCalls()
            networkService = null
            networkClient = null
        }
    }

private var networkService: NetworkService? = null
val service: NetworkService
    get() {
        return networkService
            ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(NetworkService::class.java).also {
                    networkService = it
                }
    }
const val timeOut = 10L
private var networkClient: OkHttpClient? = null
val client: OkHttpClient
    get() {
        return networkClient ?: OkHttpClient.Builder()
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .build().also {
                networkClient = it
            }
    }

