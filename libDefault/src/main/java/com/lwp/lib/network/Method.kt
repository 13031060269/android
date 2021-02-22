package com.lwp.lib.network

import androidx.annotation.IntDef

const val GET = 0x0
const val POST = 0x1
const val FORM = 0x2

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)

@IntDef(GET, POST, FORM)
@Retention(AnnotationRetention.SOURCE)
annotation class Method




