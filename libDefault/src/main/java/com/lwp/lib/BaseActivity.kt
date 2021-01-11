package com.lwp.lib

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.lwp.lib.databinding.LibLwpActivityBaseBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind
import com.lwp.lib.utils.ForResultHelper

abstract class BaseActivity : AppCompatActivity(), GainLayout {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        LibLwpActivityBaseBinding.inflate(layoutInflater)
            .apply { setContentView(root) }
            .onBind(this@BaseActivity)
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
