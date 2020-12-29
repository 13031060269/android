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

abstract class BaseFragment : Fragment(), GainLayout<BaseFragment> {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.lib_lwp_activity_base,
            container,
            false
        ).onBind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbind()
    }
}
