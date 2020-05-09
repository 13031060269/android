package com.lwp.lib.mvp

import android.view.View
import android.view.ViewStub
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lwp.lib.BR
import com.lwp.lib.R
import com.lwp.lib.utils.cast
import com.lwp.lib.utils.getGenericType

interface GainLayout<T, D : BaseModel> : That<T> where T : LifecycleOwner, T : ViewModelStoreOwner {
    val mRootModel: RootModel
        get() = getViewModel(RootModel::class.java)
    val mViewModel: D
        get() = getViewModel(getGenericType(that))

    fun getLayoutId(): Int
    fun onCreate(inflated: View)

    fun bind(view: View, viewModel: ViewModel = mRootModel): ViewDataBinding? =
        DataBindingUtil.bind<ViewDataBinding>(view)?.apply {
            lifecycleOwner = that
            setVariable(BR.data, viewModel)

            view.findViewById<ViewStub>(R.id.base_root)?.apply {
                layoutResource = getLayoutId()
                inflate()?.apply {
                    try {
                        bind(this, mViewModel)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onCreate(this)
                }
            }

            if (viewModel is BaseModel) {
                with(viewModel) {
                    rootModel = mRootModel
                    onCreate()
                }
            }
        }

    fun <D : ViewModel> getViewModel(modelClass: Class<D>): D {
        return ViewModelProvider(that).get(modelClass)
    }
}