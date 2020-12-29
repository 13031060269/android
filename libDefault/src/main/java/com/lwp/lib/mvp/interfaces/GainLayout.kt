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
import com.lwp.lib.DataBindingHelper
import com.lwp.lib.R
import com.lwp.lib.mvp.view_model.LwpViewModel
import com.lwp.lib.mvp.view_model.UIViewModel
import com.lwp.lib.utils.cast

interface GainLayout<T> : Factory where T : LifecycleOwner, T : ViewModelStoreOwner {
    val that: T
        get() = cast(this)
    val list: ArrayList<ViewDataBinding>
        get() = ArrayList()
    val provider: ViewModelProvider
        get() = ViewModelProvider(that)
    val uIViewModel: UIViewModel
        get() = provider.get(UIViewModel::class.java)

    override fun <T> create(clazz: Class<T>): T {
        if (ViewModel::class.java.isAssignableFrom(clazz)) {
            return cast(provider.get(clazz.asSubclass(ViewModel::class.java)))
        }
        return clazz.newInstance()
    }

    fun getLayoutId(): Int
    fun onBind(viewDataBinding: ViewDataBinding, viewStub: ViewStub?) {
        uIViewModel.mContext = viewDataBinding.root.context
        list.add(viewDataBinding)
        viewDataBinding.setVariable(BR.data, uIViewModel)
        (viewStub ?: viewDataBinding.root.findViewById(R.id.view_stub))?.apply {
            layoutResource = getLayoutId()
            inflate(this)
        }
    }

    fun bind(
        viewDataBinding: ViewDataBinding?,
    ) = viewDataBinding?.apply {
        list.add(this)
        lifecycleOwner = that
        DataBindingHelper.attach(this, this@GainLayout).apply {
            invoke().forEach {
                if (it is LwpViewModel<*>) {
                    it.attach(uIViewModel, that.lifecycle) {
                        invoke()
                    }
                }
            }
        }
        forEachViewStub(root as ViewGroup)
    }

    private fun forEachViewStub(viewGroup: ViewGroup) {
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
                    try {
                        bind(DataBindingUtil.bind(this))
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun unbind() {
        list.forEach {
            it.unbind()
        }

    }
}

fun ViewDataBinding?.onBind(gainLayout: GainLayout<*>, viewStub: ViewStub? = null) = this?.run {
    gainLayout.onBind(this, viewStub)
    root
}