package com.lwp.lib

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lwp.lib.mvp.RootModel
import com.lwp.lib.utils.ForResultHelper
import kotlinx.android.synthetic.main.lib_lwp_lactivity_base.*

abstract class BaseActivity : AppCompatActivity() {
    val mRootModel: RootModel by viewModels()
    val that: BaseActivity
        get() = this

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
        onInflate(base_root.inflate())
    }

    abstract fun getLayoutId(): Int
    open fun onInflate(inflated: View) {
    }

    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }
}
