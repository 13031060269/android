package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;

/**
 * Created by SongGuangYao on 2018/5/7.
 */

public class TriangleView extends AppCompatTextView {

    private int width;
    private int height;
    private Paint paint;
    private int mTriHeight;
    private Paint paintText;

    public TriangleView(Context context) {
        super(context);
        init(context);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextSize(getTextSize());
        paint.setColor(Color.parseColor("#F8F8F8"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

//        setBackgroundResource(android.R.color.transparent);
        mTriHeight = Utils.dip2px(getContext(), 4);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height - mTriHeight);
        path.lineTo(width / 2 + mTriHeight, height - mTriHeight);
        path.lineTo(width / 2, height);
        path.lineTo(width / 2 - mTriHeight, height - mTriHeight);
        path.lineTo(0, height - mTriHeight);
        path.lineTo(0, 0);
        canvas.drawPath(path, paint);

        paintText.setColor(Color.parseColor("#a4a4a8"));
        Rect rect = new Rect();
        paintText.getTextBounds(getText().toString(), 0, getText().length(), rect);
        int center = (getHeight() - mTriHeight) / 2 + rect.height() / 2;

        canvas.drawText(getText().toString(), getPaddingLeft(), center, paintText);

    }


}
