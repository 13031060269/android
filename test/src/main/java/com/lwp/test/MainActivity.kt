package com.lwp.test

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lwp.test.databinding.ComLwpTestActivityMainBinding

open class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComLwpTestActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            btn.setOnClickListener {
                startActivity(
                    Intent(
                        applicationContext,
                        SecondActivity::class.java
                    )
                )
            }
        }
    }
}