package com.lwp.lib

import androidx.databinding.ViewDataBinding
import com.lwp.lib.mvp.interfaces.Factory

object DataBindingHelper {
    fun attach(viewDataBinding: ViewDataBinding, factory: Factory): () -> List<Any?> {
        return { listOf() }
    }
}
