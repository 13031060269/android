package com.lwp.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lwp.lib.host.HostManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val iv = ImageView(this)

        btn.setOnClickListener {
//            PluginManager.getInstance().also {
//                it.launch("/sdcard/test.apk", this)
//            }
            HostManager.launch(File("/sdcard/test.apk"))
        }
        btn_2.setOnClickListener {
            HostManager.launch(File("/sdcard/test2.apk"))
//            PluginManager.getInstance().also {
//                it.launch("/sdcard/test3.apk", this)
//            }
        }
        btn_3.setOnClickListener {
            HostManager.launch(File("/sdcard/test3.apk"))
//            PluginManager.getInstance().also {
//                it.launch("/sdcard/test3.apk", this)
//            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            );
        }

    }
}