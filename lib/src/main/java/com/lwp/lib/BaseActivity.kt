package com.lwp.lib

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lwp.lib.databinding.ActivityBaseBinding
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.mvp.RootModel
import com.lwp.lib.mvp.UiInterface
import com.lwp.lib.utils.ForResultHelper
import com.lwp.lib.utils.getGenericType
import com.lwp.lib.utils.onIO
import com.lwp.lib.utils.onUI
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.base_error.*
import kotlinx.android.synthetic.main.base_loading.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

abstract class BaseActivity<T : BaseModel> : AppCompatActivity() {
    lateinit var viewModel: T
    lateinit var mRootModel: RootModel
    lateinit var provider: ViewModelProvider

    open val that: BaseActivity<T>
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewModelProvider(this).apply {
            provider = this
            viewModel = get(getGenericType(that))
            DataBindingUtil.setContentView<ActivityBaseBinding>(
                that,
                R.layout.activity_base
            )
                .apply {
                    lifecycleOwner = that
                    mRootModel = get(RootModel::class.java)
                    rootModel = mRootModel
                }
        }
        layout_error.setOnClickListener {
            mRootModel.hideError()
            reload()
        }
        base_root.layoutResource = getLayoutId()
        base_root.inflate()
        onCreate()
    }

    fun onUI(block: suspend () -> Unit): Job = onUI(viewModel.viewModelScope) { block() }
    fun onIO(block: suspend () -> Unit): Job = onIO(viewModel.viewModelScope) { block() }
    abstract fun getLayoutId(): Int
    abstract fun onCreate()
    open fun reload() {

    }

    override fun finish() {
        ForResultHelper.execute(intent)
        super.finish()
    }
}
