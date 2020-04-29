package com.lwp.app

import android.content.Intent
import android.util.ArrayMap
import java.lang.ref.WeakReference

object ForResultHelper {
    private val results by lazy {
        ArrayMap<String, WeakReference<(Any?) -> Unit>>()
    }
    private const val resultHelperKey = "result_helper_key"
    fun putCall(intent: Intent = Intent(), result: (Any?) -> Unit): Intent {
        check()
        val currentTimeMillis = System.currentTimeMillis().toString()
        intent.putExtra(resultHelperKey, currentTimeMillis)
        results[currentTimeMillis] = WeakReference(result)
        return intent
    }

    fun execute(intent: Intent, result: Any? = null) {
        check()
        intent.getStringExtra(resultHelperKey)?.let {
            results.remove(it)?.run {
                get()?.invoke(result)
                clear()
            }
        }
    }

    private fun check() {
        val iterator = results.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.value.get() == null) {
                iterator.remove()
            }
        }
    }
}