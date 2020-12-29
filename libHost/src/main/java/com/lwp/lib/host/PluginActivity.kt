package com.lwp.lib.host

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView

class  PluginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        finish()
        setContentView(TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            text = "出错了"
            setTextColor(Color.RED)
        })
    }
}