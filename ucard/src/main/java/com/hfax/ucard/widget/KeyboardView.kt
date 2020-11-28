package com.hfax.ucard.widget

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.hfax.lib.utils.Utils
import com.hfax.ucard.modules.user.adapter.KeyboardAdapter

class KeyboardView(context: Context?, attrs: AttributeSet?, defStyle: Int) : RecyclerView(context, attrs, defStyle) {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = GridLayoutManager(context, 3)
        adapter = KeyboardAdapter()
        val padding = Utils.dip2px(context, 3f)
        setPadding(padding, padding, padding, 0)
    }

    override fun getAdapter(): KeyboardAdapter {
        return super.getAdapter() as KeyboardAdapter
    }
}