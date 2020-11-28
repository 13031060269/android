package com.hfax.ucard.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hfax.ucard.R;


/**
 */

public class ClearEditText extends AppCompatEditText {
    private final static String TAG = "EditTextWithDel";
    private Drawable imgInable;
    private Drawable imgAble;
    private Context mContext;
    /**
     * 是否获取到焦点
     */
    private boolean mHasFocus;
    private boolean isShowDel;

    public ClearEditText(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        if (mContext != null) {
            init(attrs, defStyle);
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        //是否显示删除按钮
        isShowDel = a.getBoolean(R.styleable.ClearEditText_isShowDel, true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //光标获取的时候进行配置
                mHasFocus = hasFocus;
                if (isShowDel) {
                    setDrawable();
                }
            }
        });
//        //输入框提示信息字体大小
//        final float hintTextSize = a.getInteger(R.styleable.ClearEditText_hintTextSize, 14);
//        //输入框输入后字体大小
//        final float inputTextSize = a.getInteger(R.styleable.ClearEditText_inputTextSize, 18);
        imgInable = mContext.getResources().getDrawable(R.drawable.icon_edittext_del);
        imgAble = mContext.getResources().getDrawable(R.drawable.icon_edittext_del);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP, hintTextSize);
//                } else if (getTextSize() != inputTextSize) {
//                    setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize);
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isShowDel) {
                    setDrawable();
                }
            }
        });
        if (isShowDel) {
            setDrawable();
        }
        a.recycle();
    }

    //设置删除图片
    private void setDrawable() {
        if (length() > 1 && mHasFocus) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
            setCompoundDrawablePadding(5);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            setCompoundDrawablePadding(5);
        }
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if (rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
