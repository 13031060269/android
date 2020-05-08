package com.lwp.lib.utils

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.lwp.lib.network.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


const val GET = 0x0
const val POST = 0x1
const val FORM = 0x2
const val SUCCESS = GET


var BASE_URL = "https://m.beneucard.com/"
    set(value) {
        if (field != value) {
            field = value
            networkService = null
        }
    }

private var networkService: NetworkService? = null
val service: NetworkService
    get() {
        return networkService
            ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(NetworkService::class.java).also {
                    networkService = it
                }
    }


