package com.lwp.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lwp.lib.BaseActivity
import com.lwp.lib.host.HostManager
import com.lwp.lib.mvp.view_model.BaseViewModel
import com.lwp.lib.network.LwpRequestBodyDelay
import com.lwp.lib.utils.POST
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                );
            }
        }

    }

    override fun getLayoutId(): Int = R.layout.activity_main
}

class MainViewModel : BaseViewModel<String>() {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn -> {
                showLoading()
                load<String>(
                    LwpRequestBodyDelay(
                        "Http://www.baidu.com",
                        method = POST
                    ),
                    success = {
                        dismissLoading()
                    },
                )

            }
        }
    }

    override fun reload() {
//        showLoading()
        hideError()
    }

    fun open(path: String) {
        if (context() is Activity)
            GlobalScope.launch {
                HostManager.launch(
                    HostManager.install(
                        "/sdcard/$path",
                    ),
                    context() as Activity
                )
            }
    }
}