package com.lwp.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lwp.lib.databinding.LibLwpActivityBaseBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind

abstract class BaseFragment : Fragment(), GainLayout {
    override val provider: ViewModelProvider by lazy { ViewModelProvider(this) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LibLwpActivityBaseBinding.inflate(inflater).onBind(this).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbind()
    }
}
