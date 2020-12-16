package com.lwp.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind
import com.lwp.lib.mvp.view_model.UIViewModel
import com.lwp.lib.utils.ForResultHelper
import kotlinx.android.synthetic.main.lib_lwp_activity_base.*

abstract class BaseActivity : AppCompatActivity(), GainLayout<BaseActivity> {
    override val uIViewModel: UIViewModel by lazy {
        getViewModel(UIViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.lib_lwp_activity_base)
            .onBind(this,view_stub)
    }

    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbind()
    }
}
