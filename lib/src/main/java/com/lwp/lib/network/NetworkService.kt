package com.lwp.lib.network

import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface NetworkService {
    @GET("{path}")
    fun getAsync(
        @Path(value = "path", encoded = true) path: String,
        @QueryMap map: Map<String, Any?>
    ): Deferred<LwpResponseBody<Any?>>

    @POST("{path}")
    fun postAsync(
        @Path(value = "path", encoded = true) path: String,
        @Body map: Map<String, Any?>
    ): Deferred<LwpResponseBody<Any?>>

    @POST("{path}")
    @FormUrlEncoded
    fun formAsync(
        @Path(value = "path", encoded = true) path: String,
        @FieldMap map: Map<String, Any?>
    ): Deferred<LwpResponseBody<Any?>>
}