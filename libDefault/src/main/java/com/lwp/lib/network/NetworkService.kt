package com.lwp.lib.network

import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface NetworkService {
    @GET("{path}")
    fun getAsync(
        @Path(value = "path", encoded = true) path: String,
        @QueryMap map: MutableMap<String, Any>
    ): Deferred<String>

    @POST("{path}")
    fun postAsync(
        @Path(value = "path", encoded = true) path: String,
        @Body map: MutableMap<String, Any>
    ): Deferred<String>

    @POST("{path}")
    @FormUrlEncoded
    fun formAsync(
        @Path(value = "path", encoded = true) path: String,
        @FieldMap map: MutableMap<String, Any>
    ): Deferred<String>
}