package com.lwp.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lwp.lib.databinding.LibLwpActivityBaseBinding
import com.lwp.lib.mvp.interfaces.GainLayout
import com.lwp.lib.mvp.interfaces.onBind

abstract class BaseFragment : Fragment(), GainLayout {
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
