package com.lwp.lib.mvp.interfaces

import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lwp.lib.BR
import com.lwp.lib.R
import com.lwp.lib.mvp.view_model.LwpViewModel
import com.lwp.lib.mvp.view_model.UIViewModel
import com.lwp.lib.utils.cast
import com.lwp.lib.utils.findField
import java.lang.RuntimeException

interface GainLayout<T> where T : LifecycleOwner, T : ViewModelStoreOwner {
    val that: T
        get() = cast(this)
    val list: ArrayList<ViewDataBinding>
        get() = ArrayList()
    val uIViewModel: UIViewModel
        get() = getViewModel(UIViewModel::class.java)

    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding,viewStub: ViewStub?) {
        uIViewModel.mContext = viewDataBinding.root.context
        viewStub?.apply {
            layoutResource = getLayoutId()
            inflate(this)
        }
        bind(viewDataBinding, uIViewModel)
    }

    fun bind(
        viewDataBinding: ViewDataBinding?,
        viewModel: ViewModel? = null
    ) = viewDataBinding?.apply {
        list.add(this)
        lifecycleOwner = that
        setVariable(BR.data, viewModel ?: getViewModel(this))
        forEachViewStub(root as ViewGroup)
    }

    fun getViewModel(viewDataBinding: ViewDataBinding): LwpViewModel<*>? =
        findField(viewDataBinding::class.java, "mData")?.type?.asSubclass(
            LwpViewModel::class.java
        )?.run {
            getViewModel(this)
                .apply {
                    attach(uIViewModel, that.lifecycle)
                }
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

fun ViewDataBinding?.onBind(gainLayout: GainLayout<*>,viewStub: ViewStub?) = this?.run {
    gainLayout.onBind(this,viewStub)
    root
}