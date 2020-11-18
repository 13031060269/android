package com.lwp.lib.mvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lwp.lib.network.LwpRequestBody
import com.lwp.lib.network.LwpResponseBody
import com.lwp.lib.network.loadData
import com.lwp.lib.utils.*
import kotlinx.coroutines.Job

abstract class BaseModel : ViewModel() {
    var rootModel: RootModel? = null
    abstract fun onCreate()//

    inline fun loadString(
        requestBody: LwpRequestBody,
        crossinline success: (String?) -> Unit,
        crossinline error: (HttpException) -> Unit = {
            rootModel?.dismissLoading()
            rootModel?.showToast(it.msg)
        }
    ) {
        onUI {
            try {
                val body = loadData<String>(requestBody)
                success(body)
            } catch (e: HttpException) {
                error(e)
            }
        }
    }

    inline fun <reified T> loadResponseBodyData(
        requestBody: LwpRequestBody,
        crossinline success: (T?) -> Unit,
        crossinline error: (HttpException) -> Unit = {
            rootModel?.dismissLoading()
            rootModel?.showToast(it.msg)
        }
    ) {
        onIO {
            try {
                val body = loadData<LwpResponseBody<String>>(requestBody)
                if (body.errCode == SUCCESS) {
                    var fromJson: T? = null
                    body.data?.also {
                        fromJson = fromJson<T>(body.data)
                    }
                    onUI { success(fromJson) }
                } else {
                    throw HttpException(body.errMsg, body.errCode)
                }

            } catch (e: HttpException) {
                onUI { error(e) }
            }
        }

    }


    override fun onCleared() {
        rootModel = null
    }

    fun onUI(block: suspend () -> Unit): Job =
        onUI(viewModelScope) { block() }

    fun onIO(block: suspend () -> Unit): Job =
        onIO(viewModelScope) { block() }
}

data class HttpException(val msg: String?, val code: Int = SUCCESS) : Exception(msg)