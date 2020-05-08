package com.lwp.app

import android.content.Context
import com.lwp.lib.BaseActivity
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.network.LwpRequestBody

class MyModel : BaseModel() {
    override fun initData() {
    }

    fun toGranted(context: Context) {
        val body = LwpRequestBody("hfas/sapi/v2/index/query-loan-status", mutableMapOf())
        loadData<Any>(body, {
            println("it=======$it")
        })
//        val localIntent = Intent()
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
//        localIntent.data = Uri.fromParts("package", context.packageName, null)
//        context.startActivity(localIntent)
    }
}

class MainActivity : BaseActivity<MyModel>() {
    override fun getLayoutId(): Int = R.layout.activity_main
}