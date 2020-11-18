package com.lwp.lib.mvp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val UI_LOADING = "加载中..."
const val UI_NET_ERROR = "网络错误。"

interface UiInterface {
    fun showLoading(text: String = UI_LOADING)
    fun dismissLoading()
    fun showError(text: String = UI_NET_ERROR)
    fun hideError()
    fun showToast(msg: String?)


    suspend fun showUiLoading(text: String = UI_LOADING) {
        withContext(Dispatchers.Main) {
            showLoading(text)
        }
    }

    suspend fun dismissUiLoading() {
        withContext(Dispatchers.Main) {
            dismissLoading()
        }
    }

    suspend fun showUiError(text: String = UI_NET_ERROR) {
        withContext(Dispatchers.Main) {
            showError(text)
        }
    }

    suspend fun hideUiError() {
        withContext(Dispatchers.Main) {
            hideError()
        }
    }
}