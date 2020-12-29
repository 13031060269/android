package com.lwp.app

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lwp.app.databinding.ActivityStartBinding
import com.lwp.lib.BaseActivity
import kotlinx.coroutines.*

data class UserBean @JvmOverloads constructor(
    val userName: String = "leavesC",
    val userAge: Int = 0
)

class StartActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_start

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = ActivityStartBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val delay = 0
                repeat(delay) {
                    withContext(Dispatchers.Main) {
                        inflate.tv.text = "${delay - it}"
                    }
                    delay(1000)
                }
            }
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }

    }
}