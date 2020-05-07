package com.lwp.lib.utils

import com.google.gson.Gson

val gson = Gson()
inline fun <reified T> fromJson(json: Any?): T = gson.fromJson(gson.toJson(json), T::class.java)