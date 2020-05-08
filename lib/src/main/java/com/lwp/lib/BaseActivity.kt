package com.lwp.lib

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.mvp.RootModel
import com.lwp.lib.utils.ForResultHelper
import com.lwp.lib.utils.getGenericType
import kotlinx.android.synthetic.main.lib_lwp_lactivity_base.*

abstract class BaseActivity<T : BaseModel> : AppCompatActivity() {
    val that: BaseActivity<T>
        get() = this
    private val mRootModel: RootModel by viewModels()
    private val viewModel: T by lazy {
        (ViewModelProvider(that).get(getGenericType(that)) as T).apply {
            rootModel = mRootModel
        }
    }
    lateinit var provider: ViewModelProvider
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ViewDataBinding>(
            that,
            R.layout.lib_lwp_lactivity_base
        )?.apply {
            lifecycleOwner = that
            setVariable(BR.rootModel, mRootModel)
        }
        base_root.layoutResource = getLayoutId()
        base_root.setOnInflateListener { _, inflated ->
            DataBindingUtil.bind<ViewDataBinding>(inflated)?.apply {
                lifecycleOwner = that
                setVariable(BR.data, viewModel)
                viewModel.initData()
            }
        }
        base_root.inflate()

    }

    abstract fun getLayoutId(): Int
    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }
}
