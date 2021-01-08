package com.lwp.lib.mvp.view_model

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.lwp.annotation.Binding
import com.lwp.lib.mvp.interfaces.UiInterface
import com.lwp.lib.utils.clearVar
import com.lwp.lib.utils.generality

@Binding
abstract class LwpViewModel<T> : ViewModel(), UiInterface, LifecycleEventObserver {
    val model: T by lazy { initModel() }
    lateinit var functionFlush: () -> Unit
    open fun initModel(): T = generality()
    private var mBase: LwpViewModel<*>? = null
    open fun context(): Context? = mBase?.context()
    var lifecycle: Lifecycle? = null
    fun attach(mBase: LwpViewModel<*>, lifecycle: Lifecycle) {
        if (this.mBase != null) return
        this.mBase = mBase
        this.lifecycle = lifecycle
        lifecycle.addObserver(this)
    }

    fun flush() {
        functionFlush()
    }

    override fun onCleared() {
        mBase = null
        lifecycle?.removeObserver(this)
        lifecycle = null
        clearVar()
    }

    override fun showLoading(text: String) {
        mBase?.showLoading(text)
    }

    override fun dismissLoading() {
        mBase?.dismissLoading()
    }

    override fun showError(text: String, uiInterface: UiInterface) {
        mBase?.showError(text, uiInterface)
    }

    override fun hideError() {
        mBase?.hideError()
    }

    override fun showToast(msg: String?) {
        mBase?.showToast(msg)
    }

    override fun reload() {
        load()
    }

    open fun load() {

    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onCreate()
            }
            Lifecycle.Event.ON_START -> {
                onStart()
            }
            Lifecycle.Event.ON_RESUME -> {
                onResume()
            }
            Lifecycle.Event.ON_PAUSE -> {
                onPause()
            }
            Lifecycle.Event.ON_STOP -> {
                onStop()
            }
            Lifecycle.Event.ON_DESTROY -> {
                onDestroy()
            }
            Lifecycle.Event.ON_ANY -> {
            }
        }
    }

    open fun onCreate() {
        load()
    }

    open fun onStart() {
    }

    open fun onResume() {
    }

    open fun onPause() {
    }

    open fun onStop() {
    }

    open fun onDestroy() {
    }
}