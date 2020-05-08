package com.lwp.lib

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.utils.getGenericType

abstract class BaseModelActivity<T : BaseModel> : BaseActivity() {
    private val viewModel: T by lazy {
        (ViewModelProvider(that).get(getGenericType(that)) as T).apply {
            rootModel = mRootModel
        }
    }

    override fun onInflate(inflated: View) {
        DataBindingUtil.bind<ViewDataBinding>(inflated)?.apply {
            lifecycleOwner = that
            setVariable(BR.data, viewModel)
            viewModel.onCreate()
        }
    }
}
