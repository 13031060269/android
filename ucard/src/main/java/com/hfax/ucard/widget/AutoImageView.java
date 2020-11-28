package com.hfax.ucard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hfax.ucard.R;


/**
 * Author：li ChuanWu on 2017/8/14
 * Blog  ：http://blog.csdn.net/lsyz0021/
 */
public class AutoImageView extends ImageView {

    private float widthWeight = 1.0f;
    private float heightWeight = 1.0f;

    public AutoImageView(Context context) {
        super(context);

    }

    public AutoImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoImageView);
        widthWeight = a.getFloat(R.styleable.AutoImageView_widthWeight, 1.0f);
        heightWeight = a.getFloat(R.styleable.AutoImageView_heightWeight, 1.0f);
        a.recycle();
        setScaleType(ScaleType.FIT_XY);
    }

    public AutoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoImageView);
        widthWeight = a.getFloat(R.styleable.AutoImageView_widthWeight, 1.0f);
        heightWeight = a.getFloat(R.styleable.AutoImageView_heightWeight, 1.0f);
        a.recycle();
        setScaleType(ScaleType.FIT_XY);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Drawable drawable = getDrawable();

        if (drawable != null) {
            //获取真实的宽
            int minimumWidth = drawable.getMinimumWidth();
            //获取真实的高
            int minimumHeight = drawable.getMinimumHeight();
            //计算宽和高的比例
            float scale = (float) minimumHeight / minimumWidth;
            if (heightWeight != 1.0f || widthWeight != 1.0f) {
                scale = heightWeight / widthWeight;
            }
            //获取测量宽的规则
            int withsize = MeasureSpec.getSize(widthMeasureSpec);
            //按照比例计算高的测量规则
            int heightsize = (int) (withsize * scale);
            //设置高的测量规则 第一个值是按照比例计算的高 第二个参数是测量模式 精确
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightsize, MeasureSpec.EXACTLY);

        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
