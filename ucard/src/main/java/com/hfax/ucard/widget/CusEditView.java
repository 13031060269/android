package com.hfax.ucard.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.hfax.ucard.modules.entrance.UCardApplication;

/**
 * 带字体的EditView
 * Created by SongGuangYao on 2018/9/28.
 */

public class CusEditView extends AppCompatEditText {
    public CusEditView(Context context) {
        this(context, null);
    }

    public CusEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(UCardApplication.getAlternateBoldTtf());
    }
}
