package com.lwp.lib.mvp.view_model

import android.content.Context
import android.view.View
import android.widget.Toast
import com.lwp.lib.mvp.interfaces.UiInterface
import com.lwp.lib.mvp.model.UIModel

class UIViewModel : LwpViewModel<UIModel>() {
    var mContext: Context? = null
    var reloadInterface: UiInterface? = null
    override fun context(): Context? = mContext

    override fun reload() {
        reloadInterface?.reload()
        reloadInterface = null
    }

    override fun showLoading(text: String) {
        model.loading.value = View.VISIBLE
        model.loadingText.value = text
    }

    override fun dismissLoading() {
        model.loading.value = View.GONE
    }

    override fun showError(text: String, uiInterface: UiInterface) {
        this.reloadInterface = uiInterface
        model.error.value = View.VISIBLE
        model.loading.value = View.GONE
        model.errorText.value = text
    }

    override fun hideError() {
        model.error.value = View.GONE
    }

    override fun showToast(msg: String?) {
        context().apply {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        reloadInterface = null
        mContext = null
    }
}