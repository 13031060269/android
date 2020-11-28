package com.hfax.ucard.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.hfax.ucard.modules.entrance.UCardApplication;

/**
 * 带字体的EditView
 * Created by SongGuangYao on 2018/9/28.
 */

public class CusRaidoButton extends AppCompatRadioButton {
    public CusRaidoButton(Context context) {
        this(context,null);
    }

    public CusRaidoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(UCardApplication.getAlternateBoldTtf());
    }

}
