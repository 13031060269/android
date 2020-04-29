package com.lwp.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.base_error.*
import kotlinx.android.synthetic.main.base_loading.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    open fun beforeContentView() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeContentView()
        setContentView(R.layout.activity_base)
        layoutInflater.inflate(getLayoutId(), base_root, true)
        layout_error.setOnClickListener {
            reload()
        }
        onCreate()
    }

    abstract fun getLayoutId(): Int
    abstract fun onCreate()
    open fun reload() {

    }

    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }

    inline fun showError(text: String = getString(R.string.error_msg)) {
        hideLoading()
        layout_error.visibility = View.VISIBLE
        error_msg.text = text
    }

    inline fun hideError() {
        layout_error.visibility = View.GONE
    }

    inline fun showLoading(text: String = getString(R.string.loading_msg)) {
        hideError()
        layout_loading.visibility = View.VISIBLE
        loading_msg.text = text
    }

    inline fun hideLoading() {
        layout_loading.visibility = View.GONE
    }

    suspend inline fun showErrorAsync(text: String = getString(R.string.error_msg)) {
        withContext(Dispatchers.Main) {
            showError(text)
        }
    }

    suspend inline fun hideErrorAsync() {
        withContext(Dispatchers.Main) {
            hideError()
        }
    }

    suspend inline fun showLoadingAsync(text: String = getString(R.string.loading_msg)) {
        withContext(Dispatchers.Main) {
            showLoading(text)
        }
    }

    suspend inline fun hideLoadingAsync() {
        withContext(Dispatchers.Main) {
            hideLoading()
        }
    }

    override fun onClick(v: View?) {
    }
}
