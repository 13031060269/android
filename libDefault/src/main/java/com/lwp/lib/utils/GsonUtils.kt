package com.lwp.lib.utils

import com.google.gson.Gson

val gson = Gson()
inline fun <reified T> fromJson(json: String?): T = gson.fromJson(json, T::class.java)
inline fun toJson(bean: Any?): String = gson.toJson(bean)