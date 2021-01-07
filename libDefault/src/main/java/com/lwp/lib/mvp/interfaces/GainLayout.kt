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
import com.lwp.lib.utils.cast
import com.lwp.lib.utils.clearVar
import com.lwp.lib.utils.getVar
import com.lwp.lib.utils.saveVar

internal interface GainLayout : Factory, LifecycleOwner, ViewModelStoreOwner {

    val list: ArrayList<ViewDataBinding>
        get() = getVar() ?: synchronized(this) {
            val arrayList = ArrayList<ViewDataBinding>()
            saveVar(arrayList)
            arrayList
        }

    val provider: ViewModelProvider
    val uIViewModel: UIViewModel
        get() = provider.get(UIViewModel::class.java)

    override fun <T : LwpViewModel<*>> create(clazz: Class<T>): T? {
        return cast<T>(provider.get(clazz.asSubclass(ViewModel::class.java))).apply {
            attach(uIViewModel, this@GainLayout.lifecycle)
        }
    }

    @LayoutRes
    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding, viewStubProxy: ViewStubProxy?) {
        uIViewModel.mContext = viewDataBinding.root.context
        viewDataBinding.lifecycleOwner = this
        list.add(viewDataBinding)
        inflateViewStubProxy(viewStubProxy, getLayoutId())
    }

    fun inflateViewStubProxy(viewStubProxy: ViewStubProxy?, @LayoutRes layoutId: Int = 0) {
        viewStubProxy?.apply {
            setOnInflateListener { _, _ ->
                binding?.apply {
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
        }
        clearVar()
    }
}

internal fun LibLwpActivityBaseBinding.onBind(gainLayout: GainLayout) =
    this.apply {
        gainLayout.onBind(this, viewStub)
        data = gainLayout.uIViewModel
    }
