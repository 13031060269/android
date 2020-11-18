package com.lwp.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lwp.lib.host.HostManager
import com.lwp.lib.plugin.PluginManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            HostManager.getInstance(application).also {
                it.launch(File("/sdcard/test.apk"))
            }
        }
//        btn_2.setOnClickListener {
//            PluginManager.getInstance().also {
//                it.launch("/sdcard/test3.apk", this)
//            }
//        }
    }
}