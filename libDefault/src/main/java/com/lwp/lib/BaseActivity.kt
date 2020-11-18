package com.lwp.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.mvp.GainLayout
import com.lwp.lib.utils.ForResultHelper
import kotlinx.android.synthetic.main.lib_lwp_activity_base.*

abstract class BaseActivity<T : BaseModel> : FragmentActivity(), GainLayout<BaseActivity<T>, T> {
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_lwp_activity_base)
        bind(root)
    }

    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }
}
