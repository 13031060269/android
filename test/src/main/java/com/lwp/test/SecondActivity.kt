package com.lwp.test

import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lwp.test.databinding.ComLwpTestActivitySecondBinding

open class SecondActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComLwpTestActivitySecondBinding.inflate(layoutInflater).apply {
            setContentView(root)
            btn.setOnClickListener { finish() }
        }
    }
}