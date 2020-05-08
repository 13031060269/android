package com.lwp.lib.mvp

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RootModel : ViewModel(), UiInterface {
    val showLoading = MutableLiveData(View.GONE)
    val loadingText = MutableLiveData(UI_LOADING)
    val showError = MutableLiveData(View.GONE)
    val errorText = MutableLiveData(UI_NET_ERROR)
    override fun showLoading(text: String) {
        showLoading.value = View.VISIBLE
        loadingText.value = text
    }

    override fun dismissLoading() {
        showLoading.value = View.GONE
    }

    override fun showError(text: String) {
        showError.value = View.VISIBLE
        showLoading.value = View.GONE
        errorText.value = text
    }

    override fun hideError() {
        showError.value = View.GONE
    }

    override fun showToast(msg: String?) {
        println("--------------showToast:$msg-------------------")
//        Toast.makeText(, msg, Toast.LENGTH_SHORT).show()
    }
}