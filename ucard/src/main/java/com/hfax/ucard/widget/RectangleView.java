package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * 可控制长度的圆角矩形
 *
 * @author SongGuangyao
 * @date 2018/4/25
 */

public class RectangleView extends AppCompatTextView {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RectangleView(Context context) {
        super(context);
        init();
    }

    public RectangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 加载
     */
    private void init() {
        setBackgroundResource(android.R.color.transparent);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FAFAFA"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), getHeight() / 2, getHeight() / 2, mPaint);
    }
}
