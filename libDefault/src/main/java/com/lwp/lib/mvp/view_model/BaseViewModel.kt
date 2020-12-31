package com.lwp.lib.mvp.view_model

import android.view.View
import androidx.lifecycle.viewModelScope
import com.lwp.lib.network.LwpRequestBody
import com.lwp.lib.network.LwpResponseBody
import com.lwp.lib.network.loadData
import com.lwp.lib.utils.CoroutineUtils
import com.lwp.lib.utils.SUCCESS
import com.lwp.lib.utils.fromJson

abstract class BaseViewModel<T> : LwpViewModel<T>() {
    inline fun <reified D> load(
        requestBody: LwpRequestBody,
        crossinline success: (D?) -> Unit,
        crossinline error: (HttpException) -> Unit = {
            showError()
            showToast(it.msg)
        },
    ) {
        onIO {
            try {
                val body = onIo { loadData<D>(requestBody) }
                onUi {
                    success(body)
                }
            } catch (e: HttpException) {
                onUi {
                    error(e)
                }
            }
        }
    }

    open fun onClick(view: View) {

    }

    inline fun <reified D> loadResponseBodyData(
        requestBody: LwpRequestBody,
        crossinline success: (D?) -> Unit,
        crossinline error: (HttpException) -> Unit = {
            dismissLoading()
            showToast(it.msg)
        }
    ) {
        onIO {
            try {
                val body = loadData<LwpResponseBody<String>>(requestBody)
                if (body.errCode == SUCCESS) {
                    var fromJson: D? = null
                    body.data?.also {
                        fromJson = fromJson<D>(body.data)
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

    fun onUI(block: suspend () -> Unit) = CoroutineUtils.onUI(viewModelScope, block)
    fun onIO(block: suspend () -> Unit) = CoroutineUtils.onIO(viewModelScope, block)
    suspend fun <T> onUi(block: suspend () -> T): T = CoroutineUtils.onUi(block)
    suspend fun <T> onIo(block: suspend () -> T): T = CoroutineUtils.onIo(block)

}

data class HttpException(val msg: String?, val code: Int = SUCCESS) : Exception(msg)