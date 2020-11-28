package com.hfax.ucard.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;

public class GuideBottomView extends RelativeLayout {
    int position = 0;
    int size = 4;
    int width;
    Paint paintBg = new Paint();
    final static int dip = Utils.dip2px(BaseApplication.getContext(), 1);
    final static int bgHight = dip;
    View highLight;
    int dur = 300;

    public GuideBottomView(Context context) {
        super(context);
    }

    public GuideBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        paintBg.setColor(0xFFE6E8F2);
        setWillNotDraw(false);
        highLight = new View(getContext());
        highLight.setBackgroundColor(0xFFFF9A18);
        addView(highLight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (size == 0) return;
        canvas.drawRect(0, (getHeight() - bgHight) / 2, getWidth(), (getHeight() + bgHight) / 2, paintBg);
        if (width != getWidth() / size) {
            width = getWidth() / size;
            LayoutParams lp = new LayoutParams(width, getHeight());
            highLight.setLayoutParams(lp);
        }
        homing(0);
    }

    public void setSize(int size) {
        this.size = size;
        postInvalidate();
    }

    public void setPosition(int position) {
        this.position = position;
        homing(dur);
    }

    void homing(int dur) {
        ObjectAnimator.ofFloat(highLight, "translationX", position * width).setDuration(dur).start();
    }
}
