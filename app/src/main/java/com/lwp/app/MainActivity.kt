package com.lwp.app

import com.lwp.app.utils.onIO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay


class MainActivity : BaseActivity() {

    override fun onCreate() {
        fab.setOnClickListener {
            reload()
        }
        ForResultHelper.putCall(intent) { println("===========finish============") }
    }

    override fun reload() = onIO {
        showLoadingAsync()
        delay(2000)
        hideLoadingAsync()
    }

    override fun getLayoutId(): Int = R.layout.activity_main

}
