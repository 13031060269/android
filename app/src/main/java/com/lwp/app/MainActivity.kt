package com.lwp.app

import android.content.Context
import android.view.View
import com.lwp.lib.BaseActivity
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.network.LwpRequestBody

class MyModel : BaseModel() {
    override fun onCreate() {
    }

    fun toGranted(context: Context) {
        val body = LwpRequestBody("hfas/sapi/v2/index/query-loan-status", mutableMapOf())
        loadResponseBodyData<Any>(body, {
            println("it=======$it")
        }){
            println("error=======$it")
        }
//        val localIntent = Intent()
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
//        localIntent.data = Uri.fromParts("package", context.packageName, null)
//        context.startActivity(localIntent)
    }
}

class MainActivity : BaseActivity<MyModel>() {
    override fun getLayoutId(): Int = R.layout.activity_main
    override fun onCreate(inflated: View) {
//        fab.setOnClickListener {
//            println("---------------fab--------------------")
//        }
    }
}