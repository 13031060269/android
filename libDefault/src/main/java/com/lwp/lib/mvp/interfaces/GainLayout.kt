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

internal interface GainLayout : Factory, LifecycleOwner, ViewModelStoreOwner {

    val list: ArrayList<ViewDataBinding>
        get() = lazyVar { ArrayList() }

    val provider: ViewModelProvider
        get() = lazyVar { ViewModelProvider(this) }
    val uIViewModel: UIViewModel
        get() = lazyVar { provider.get(UIViewModel::class.java) }

    override fun <T> create(clazz: Class<T>): T? {
        return try {
            cast<T>(provider.get(clazz.asSubclass(ViewModel::class.java))).apply {
                if (this is LwpViewModel<*>) {
                    attach(uIViewModel, this@GainLayout.lifecycle)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    @LayoutRes
    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding, viewStubProxy: ViewStubProxy?) {
        uIViewModel.mContext = viewDataBinding.root.context
        viewDataBinding.lifecycleOwner = this
        list.add(viewDataBinding)
        interceptInflate(viewStubProxy, getLayoutId())
    }

    fun interceptInflate(viewStubProxy: ViewStubProxy?, @LayoutRes layoutId: Int = 0) {
        viewStubProxy?.apply {
            setOnInflateListener { _, _ ->
                binding?.apply {
                    list.add(this)
                    lifecycleOwner = this@GainLayout
                    variableMapper.attachToDataBinding(this, this@GainLayout)
                }
            }
            viewStub?.apply {
                if (layoutId != 0) {
                    layoutResource = layoutId
                }
                inflate()
            }
        }
    }

    fun unbind() {
        list.forEach {
            it.unbind()
            it.clearVar()
        }
        list.clear()
        clearVar()
    }
}

internal fun LibLwpActivityBaseBinding.onBind(gainLayout: GainLayout) =
    this.apply {
        gainLayout.onBind(this, viewStub)
        data = gainLayout.uIViewModel
    }
