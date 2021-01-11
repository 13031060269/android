package com.lwp.lib.mvp.view_model

import androidx.lifecycle.MutableLiveData
import com.lwp.lib.utils.generality

abstract class BaseLiveDataViewModel<T> : BaseViewModel<MutableLiveData<T>>() {
    final override fun initModel(): MutableLiveData<T> = MutableLiveData(value)
    val value: T by lazy { initValue() }
    open fun initValue(): T = generality()
    fun flush(block: T.() -> Unit) {
        model.value?.apply {
            block()
            model.postValue(this)
        }
    }
}