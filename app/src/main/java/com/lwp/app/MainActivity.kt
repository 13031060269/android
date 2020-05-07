package com.lwp.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.lwp.lib.BaseActivity
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.mvp.RootModel
import com.lwp.lib.utils.onIO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay

class MyModel : BaseModel() {

}

class MainActivity : BaseActivity<MyModel>() {
    override fun onCreate() {
        fab.setOnClickListener {
//            toGranted(this@MainActivity)
            reload()
        }
    }

    override fun reload() {
        onIO {
            mRootModel.showUiLoading()
            delay(2000)
            mRootModel.dismissUiLoading()
        }
    }

    private fun toGranted(context: Context) {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", packageName, null)
        context.startActivity(localIntent)
    }

    override fun getLayoutId(): Int = R.layout.activity_main
}
