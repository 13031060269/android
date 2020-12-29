package com.lwp.lib.network

import androidx.annotation.IntDef
import com.lwp.lib.utils.*

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@IntDef(value = [GET, POST, FORM])
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Method