package com.lwp.lib.mvp.view_model

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.lwp.lib.mvp.interfaces.UiInterface
import com.lwp.lib.utils.generality

abstract class LwpViewModel<T> : ViewModel(), UiInterface, LifecycleEventObserver {
    val model: T by lazy { initModel() }
    val caches = HashMap<String, Any>()
    open fun initModel(): T = generality()
    private var mBase: LwpViewModel<*>? = null
    open fun context(): Context? = mBase?.context()
    private lateinit var flushSelf: () -> Unit
    var lifecycle: Lifecycle? = null
    fun attach(mBase: LwpViewModel<*>, lifecycle: Lifecycle, _flush: () -> Unit = {}) {
        this.mBase = mBase
        this.lifecycle = lifecycle
        lifecycle.addObserver(this)
        this.flushSelf = _flush
    }

    fun flush() {
        flushSelf()
    }

    override fun onCleared() {
        mBase = null
        lifecycle?.removeObserver(this)
        lifecycle = null
        caches.clear()
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
        showLoading()
        onCreate()
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