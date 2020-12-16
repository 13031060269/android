package com.lwp.lib.mvp.interfaces

import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lwp.lib.BR
import com.lwp.lib.R
import com.lwp.lib.mvp.view_model.LwpViewModel
import com.lwp.lib.mvp.view_model.UIViewModel
import com.lwp.lib.utils.cast
import com.lwp.lib.utils.findField

interface GainLayout<T> where T : LifecycleOwner, T : ViewModelStoreOwner {
    val that: T
        get() = cast(this)
    val list: ArrayList<ViewDataBinding>
        get() = ArrayList()
    val uIViewModel: UIViewModel
        get() = getViewModel(UIViewModel::class.java)

    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding) {
        uIViewModel.mContext = viewDataBinding.root.context
        bind(viewDataBinding)
    }

    fun bind(
        viewDataBinding: ViewDataBinding?,
    ): LwpViewModel<*>? = viewDataBinding?.run {
        list.add(this)
        val clazz = findField(viewDataBinding::class.java, "mData")?.type?.asSubclass(
            LwpViewModel::class.java
        ) ?: return null
        lifecycleOwner = that
        root.findViewById<ViewStub>(R.id.view_stub)?.apply {
            layoutResource = getLayoutId()
            inflate(this)
        }
        val viewModel = getViewModel(clazz)?.apply {
            attach(uIViewModel, that.lifecycle)
        }
        setVariable(BR.data, viewModel)
        forEachViewStub(root as ViewGroup)
        viewModel
    }

    fun forEachViewStub(viewGroup: ViewGroup) {
        viewGroup.children.forEach {
            if (it is ViewGroup) {
                forEachViewStub(it)
            } else if (it is ViewStub) {
                inflate(it)
            }
        }
    }

    fun inflate(viewStub: ViewStub) {
        viewStub.apply {
            if (layoutResource != 0) {
                inflate()?.apply {
                    bind(DataBindingUtil.bind(this))
                }
            }
        }
    }

    fun <C : LwpViewModel<*>> getViewModel(modelClass: Class<C>): C {
        return ViewModelProvider(that).get(modelClass)
    }

    fun unbind() {
        list.forEach {
            it.unbind()
        }
    }
}
