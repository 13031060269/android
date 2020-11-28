package com.hfax.ucard.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.hfax.ucard.R
import com.hfax.ucard.modules.user.adapter.KeyboardListener
import java.util.*

class AuthCodeView(context: Context?, attrs: AttributeSet?, defStyle: Int) : LinearLayout(context, attrs, defStyle), KeyboardListener {
    var size: Int = 6
    val input = Stack<String>()
    private val listView = mutableListOf<TextView>()
    var codeListener: CodeListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        reset()
    }

    fun setLength(size: Int) {
        this.size = size
        if (size == 0) return
        removeAllViews()
        reset()
    }

    private fun reset() {
        input.clear()
        repeat(size) {
            var view: TextView? = null
            if (listView.size > it) {
                view = listView[it]
            }
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_auth_code, this, false) as TextView
                listView.add(it, view)
            }
            view.text = null
            view.isSelected = false
            addView(view)
        }
    }

    override fun onDelete() {
        if (input.size == size) {
            codeListener?.fullToNot()
        }
        if (input.size > 0) {
            input.pop()
            listView[input.size].apply {
                text = null
                isSelected = false
            }
        }
    }

    override fun onSelect(num: String) {
        if (input.size < size) {
            listView[input.size].apply {
                text = "$num"
                isSelected = true
            }
            input.push(num)
            if (input.size == size) {
                val sb = StringBuilder()
                input.forEach {
                    sb.append(it)
                }
                codeListener?.inputFull(sb.toString())
            }
        }
    }

    override fun onSelectX(x: String) {
    }
}

interface CodeListener {
    fun inputFull(inputs: String)
    fun fullToNot()
}
