package com.lwp.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind
import com.lwp.lib.mvp.view_model.UIViewModel
import kotlinx.android.synthetic.main.lib_lwp_activity_base.*

abstract class BaseFragment : Fragment(), GainLayout<BaseFragment> {
    override val uIViewModel: UIViewModel by lazy {
        getViewModel(UIViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.lib_lwp_activity_base,
            null,
            false
        ).onBind(this,view_stub)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbind()
    }
}
