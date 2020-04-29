package com.lwp.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val packageURI: Uri = Uri.parse("package:$packageName")
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
            intent.resolveActivity(packageManager)?.apply {
                startActivity(intent)
            }
        }
    }

}
