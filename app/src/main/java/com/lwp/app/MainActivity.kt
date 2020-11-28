package com.lwp.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lwp.lib.host.HostManager
import dalvik.system.PathClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            open("test.apk")
        }
        btn_2.setOnClickListener {
            open("test2.apk")
        }
        btn_3.setOnClickListener {
            open("test3.apk")
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
    fun open(path:String){
        GlobalScope.launch {
            HostManager.launch(
                HostManager.install("/sdcard/$path", "x86"),
                this@MainActivity
            )
        }
    }
}