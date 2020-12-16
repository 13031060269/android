package com.lwp.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.utils.ForResultHelper

abstract class BaseActivity : AppCompatActivity(), GainLayout<BaseActivity> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.lib_lwp_activity_base)
            ?.apply {
                onBind(
                    this
                )
            }
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
