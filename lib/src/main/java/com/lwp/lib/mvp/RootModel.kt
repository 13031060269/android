package com.lwp.lib.mvp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RootModel : ViewModel(), UiInterface {
    private val showLoading = MutableLiveData(false)
    private val loadingText = MutableLiveData(UI_LOADING)
    private val showError = MutableLiveData(false)
    private val errorText = MutableLiveData(UI_NET_ERROR)

    override fun showLoading(text: String) {
        showLoading.value = true
        loadingText.value = text
    }

    override fun dismissLoading() {
        showLoading.value = false
    }

    override fun showError(text: String) {
        showError.value = true
        showLoading.value = false
        errorText.value = text
    }

    override fun hideError() {
        showError.value = false
    }

    override fun showToast(msg: String) {
        println("--------------showToast:$msg-------------------")
//        Toast.makeText(, msg, Toast.LENGTH_SHORT).show()
    }
}