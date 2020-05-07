package com.lwp.lib.mvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lwp.lib.network.LwpNetwork
import com.lwp.lib.network.LwpRequestBody
import kotlinx.coroutines.launch

open class BaseModel : ViewModel() {
    var rootModel: RootModel? = null
    val lwpNetwork: LwpNetwork by lazy { LwpNetwork() }
    inline fun <reified T> loadData(
        crossinline success: (T) -> Unit,
        crossinline error: (Int, String?) -> Unit = { msg, code ->
            rootModel?.dismissLoading()
        }
    ) {
        viewModelScope.launch {
            lwpNetwork.request(LwpRequestBody("", mutableMapOf()), success, error)
        }
    }

    override fun onCleared() {
        rootModel = null
    }
}