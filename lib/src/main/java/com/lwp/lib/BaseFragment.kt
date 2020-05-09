package com.lwp.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lwp.lib.mvp.BaseModel
import com.lwp.lib.mvp.GainLayout

abstract class BaseFragment<T : BaseModel> : Fragment(), GainLayout<BaseFragment<T>, T> {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lib_lwp_activity_base, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind(view)
    }
}
