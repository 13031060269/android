package com.hfax.ucard.utils

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView


object SpannableStringUtils {
    private val click by lazy { {} }
    fun setColor(text: CharSequence, match: String, color: Int, tv: TextView, block: () -> Unit = click) {
        SpannableString(text).apply {
            val start = text.indexOf(match)
            if (start >= 0) {
                setSpan(ForegroundColorSpan(color), start, start + match.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                if (block != click) {
                    setSpan(MyClickableSpan(block), start, start + match.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    tv.movementMethod = LinkMovementMethod.getInstance();
                }
            }
            tv.text=this
        }
    }

    internal class MyClickableSpan(private val block: () -> Unit) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            block()
        }

    }
}


