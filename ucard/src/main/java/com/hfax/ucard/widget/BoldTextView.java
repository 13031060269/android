package com.hfax.ucard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.hfax.ucard.R;

/**
 * @author lwp
 * 字体加粗的textview
 */
public class BoldTextView extends AppCompatTextView {

    public BoldTextView(Context context) {
        this(context,null);
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs!=null){
            TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.HfaxAttr,defStyleAttr,0);
            boolean bold = a.getBoolean(R.styleable.HfaxAttr_bold,true);
            getPaint().setFakeBoldText(bold);
            a.recycle();
        }
    }
}
