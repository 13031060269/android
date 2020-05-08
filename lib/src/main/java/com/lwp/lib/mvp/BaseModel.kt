package com.lwp.lib.mvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lwp.lib.network.LwpNetwork
import com.lwp.lib.network.LwpRequestBody
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseModel : ViewModel() {
    var rootModel: RootModel? = null
    val lwpNetwork: LwpNetwork by lazy { LwpNetwork() }
    abstract fun initData()//
    inline fun <reified T> loadData(
        body: LwpRequestBody,
        crossinline success: (T) -> Unit,
        crossinline error: (Int, String?) -> Unit = { _, msg ->
            rootModel?.dismissLoading()
            rootModel?.showToast(msg)
        }
    ) {
        viewModelScope.launch {
            lwpNetwork.request(body, success, error)
        }
    }

    override fun onCleared() {
        rootModel = null
    }

    fun onUI(block: suspend () -> Unit): Job =
        com.lwp.lib.utils.onUI(viewModelScope) { block() }

    fun onIO(block: suspend () -> Unit): Job =
        com.lwp.lib.utils.onIO(viewModelScope) { block() }
}