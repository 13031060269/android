package com.lwp.lib.utils

import kotlinx.coroutines.*

object CoroutineUtils {
    fun onUI(scope: CoroutineScope = GlobalScope, block: suspend () -> Unit) =
        scope.launch {
            withContext(Dispatchers.Main) {
                block()
            }
        }

    suspend fun <T> onUi(block: suspend () -> T): T =
        withContext(Dispatchers.Main) {
            block()
        }

    fun onIO(scope: CoroutineScope = GlobalScope, block: suspend () -> Unit) =
        scope.launch {
            withContext(Dispatchers.IO) {
                block()
            }
        }

    suspend fun <T> onIo(block: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            block()
        }
}
