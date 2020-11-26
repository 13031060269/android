package com.lwp.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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
            GlobalScope.launch {
                HostManager.launch(HostManager.install("/sdcard/test.apk"), this@MainActivity)

            }
        }
        btn_2.setOnClickListener {
            GlobalScope.launch {
                HostManager.launch(HostManager.install("/sdcard/test2.apk"), this@MainActivity)
            }
        }
        btn_3.setOnClickListener {
//            HostManager.launch(File("/sdcard/test3.apk"))
//            startActivity(Intent(this,MainActivity::class.java))
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