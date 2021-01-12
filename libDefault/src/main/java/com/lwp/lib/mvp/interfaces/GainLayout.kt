package com.lwp.lib.mvp.interfaces

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewStubProxy
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lwp.lib.databinding.LibLwpActivityBaseBinding
import com.lwp.lib.mvp.mapper.VariableFactory.Companion.variableMapper
import com.lwp.lib.mvp.view_model.LwpViewModel
import com.lwp.lib.mvp.view_model.UIViewModel
import com.lwp.lib.utils.*

interface GainLayout : Factory, LifecycleOwner, ViewModelStoreOwner {

    val bindings: ArrayList<ViewDataBinding>
        get() = lazyVar { ArrayList() }
    private val provider: ViewModelProvider
        get() = lazyVar { ViewModelProvider(this) }
    val uIViewModel: UIViewModel
        get() = lazyVar { provider.get(UIViewModel::class.java) }

    override fun <T> create(clazz: Class<T>): T? {
        return try {
            if (ViewModel::class.java.isAssignableFrom(clazz)) {
                cast<T>(provider.get(clazz.asSubclass(ViewModel::class.java))).apply {
                    if (this is LwpViewModel<*>) {
                        attach(uIViewModel, this@GainLayout)
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    @LayoutRes
    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding) {
        uIViewModel.mContext = viewDataBinding.root.context
        viewDataBinding.lifecycleOwner = this
        bindings.add(viewDataBinding)
    }

    fun inflate(viewStubProxy: ViewStubProxy) {
        viewStubProxy.apply {
            setOnInflateListener { _, _ ->
                binding?.apply {
                    bindings.add(this)
                    lifecycleOwner = this@GainLayout
                    variableMapper.attachToDataBinding(this, this@GainLayout)
                }
            }
            viewStub?.apply {
                inflate()
            }
        }
    }

    fun create() {

    }

    fun unbind() {
        bindings.forEach {
            it.unbind()
            it.clearVar()
        }
        bindings.clear()
        clearVar()
    }
}

inline fun <reified T : ViewDataBinding> GainLayout.getViewDataBinding(): T? {
    bindings.forEach {
        if (it::class.java == T::class.java) {
            return cast(it)
        }
    }
    return null
}

internal fun LibLwpActivityBaseBinding.onBind(gainLayout: GainLayout) =
    this.apply {
        gainLayout.onBind(this)
        viewStub.apply {
            viewStub?.layoutResource = gainLayout.getLayoutId()
            gainLayout.inflate(this)
        }
        data = gainLayout.uIViewModel
        gainLayout.create()
    }
