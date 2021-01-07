package com.lwp.lib

import android.os.Bundle
import android.view.ViewStub
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.lwp.lib.databinding.LibLwpActivityBaseBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind
import com.lwp.lib.utils.ForResultHelper

abstract class BaseActivity : AppCompatActivity(), GainLayout {
    override val provider: ViewModelProvider by lazy { ViewModelProvider(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LibLwpActivityBaseBinding.inflate(layoutInflater)
            .onBind(this@BaseActivity)
            .apply {
                setContentView(root)
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
