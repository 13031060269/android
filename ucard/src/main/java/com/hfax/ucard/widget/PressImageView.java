package com.hfax.ucard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hfax.ucard.R;

public class PressImageView extends ImageView {
    private static final float transparent = 0.8f;//按下去时候图片的透明度， 取值范围 0-1；

    public PressImageView(Context context) {
        this(context, null);
    }

    public PressImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PressImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Drawable drawable = getDrawable();
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable == null || drawable instanceof StateListDrawable) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PressImageView);
        Drawable pressDrawable = a.getDrawable(R.styleable.PressImageView_press_bg);
        a.recycle();
        if (pressDrawable instanceof StateListDrawable) {
            return;
        }
        if (pressDrawable == null) {
            pressDrawable = drawable.getConstantState().newDrawable();
            pressDrawable.mutate();
            pressDrawable.setAlpha((int) (255 * transparent));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        stateListDrawable.addState(new int[]{}, drawable);
        setImageDrawable(stateListDrawable);
    }
}
