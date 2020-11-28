package com.lwp.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val delay = 3
                repeat(delay) {
                    withContext(Dispatchers.Main) {
                        tv.text = "${delay - it}"
                    }
                    delay(1000)
                }
            }
            finish()
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }
    }
}