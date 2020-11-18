package com.lwp.lib.utils

import kotlinx.coroutines.*

inline fun onUI(scope: CoroutineScope = GlobalScope, crossinline block: suspend () -> Unit): Job {
    return scope.launch {
        withContext(Dispatchers.Main) {
            block()
        }
    }
}

suspend inline fun <T> onUi(crossinline block: suspend () -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

inline fun onIO(scope: CoroutineScope = GlobalScope, crossinline block: suspend () -> Unit): Job {
    return scope.launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}

suspend inline fun <T> onIo(crossinline block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}
