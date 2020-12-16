package com.lwp.lib.mvp.model

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lwp.lib.mvp.interfaces.UI_LOADING
import com.lwp.lib.mvp.interfaces.UI_NET_ERROR

class UIModel {
    val loading = MutableLiveData(View.GONE)
    val loadingText = MutableLiveData(UI_LOADING)
    val error = MutableLiveData(View.GONE)
    val errorText = MutableLiveData(UI_NET_ERROR)
}