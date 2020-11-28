package com.hfax.ucard.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import com.hfax.ucard.R

class DelOrPWDEditText(context: Context, attrs: AttributeSet?) : EditText(context, attrs) {
    private var delBitmap: Bitmap? = null
    private var pwdBitmapNormal: Bitmap? = null
    private var pwdBitmapShow: Bitmap? = null
    private var isShow = false
    private val delRect = Rect()
    private val pwdRect = Rect()

    constructor(context: Context) : this(context, null)

    init {
        val obtain = context.obtainStyledAttributes(attrs, R.styleable.DelOrPWDEditText)
        val del = obtain.getBoolean(R.styleable.DelOrPWDEditText_del, false)
        if (del) {
            delBitmap = BitmapFactory.decodeResource(resources, R.drawable.del_et)
        }
        val pwd = obtain.getBoolean(R.styleable.DelOrPWDEditText_pwd, false)
        if (pwd) {
            pwdBitmapNormal = BitmapFactory.decodeResource(resources, R.drawable.show_et_show)
            pwdBitmapShow = BitmapFactory.decodeResource(resources, R.drawable.show_et_normal)
            inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        }
        obtain.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (text.toString().isBlank()) return
        delBitmap?.let {
            delRect.left = width
            delRect.right = width
            delRect.bottom = height
            delRect.left -= it.width * 2
            delRect.bottom = height
            canvas?.drawBitmap(it, delRect.left.toFloat() + it.width / 2, (height - it.height) / 2f, null)
        }
        pwdBitmapNormal?.let {
            pwdRect.bottom = height
            var bitmap = it
            if (isShow) {
                pwdBitmapShow?.run {
                    bitmap = this
                }
            }
            pwdRect.right = width - delRect.width()
            pwdRect.left = delRect.left - 2 * bitmap.width
            canvas?.drawBitmap(bitmap, pwdRect.left.toFloat() + bitmap.width / 2, (height - bitmap.height) / 2f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val eventX = event.x.toInt()
            val eventY = event.y.toInt()
            if (delRect.contains(eventX, eventY)) {
                setText("")
            }
            if (pwdRect.contains(eventX, eventY)) {
                isShow = !isShow
                if (!isShow) {
                    inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                } else {
                    inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

}
