package com.hfax.ucard.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.hfax.ucard.modules.entrance.UCardApplication;

/**
 * 带字体的EditView
 * Created by SongGuangYao on 2018/9/28.
 */

public class CusTextView extends AppCompatTextView {
    public CusTextView(Context context) {
        this(context, null);
    }

    public CusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(UCardApplication.getAlternateBoldTtf());
    }
}
