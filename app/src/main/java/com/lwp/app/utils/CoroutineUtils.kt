package com.lwp.app.utils

import kotlinx.coroutines.*

inline fun onUI(crossinline block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch {
        withContext(Dispatchers.Main) {
            block()
        }
    }
}

suspend inline fun <T> onUi(crossinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

inline fun onIO(crossinline block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}

suspend inline fun <T> onIo(crossinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}
