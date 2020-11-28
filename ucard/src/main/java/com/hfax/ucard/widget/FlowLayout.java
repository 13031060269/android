package com.hfax.ucard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * 自定义流式布局
 *
 * @author SongGuangyao
 * @date 2018/4/25
 */

public class FlowLayout extends RadioGroup {

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 设备密度
     */
    private float mDensity;
    /**
     * 子控件间的距离
     */
    private int horizontalSpace, verticalSpace;

    public FlowLayout(Context context) {
        super(context);
        init(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void init(Context context) {
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mDensity = context.getResources().getDisplayMetrics().density;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childCount = getChildCount();
        View child = null;
        //获取左侧起始位置
        int left = getPaddingLeft();

        //当前行的最大高度
        int maxHeightInLine = 0;
        //所有高度
        int allHeight = 0;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            //测量子控件
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //两两对比，取得一行中最大的高度
            if (child.getMeasuredHeight() > maxHeightInLine) {
                maxHeightInLine = child.getMeasuredHeight();
            }
            left += child.getMeasuredWidth() + dip2px(horizontalSpace) + child.getPaddingLeft() + child.getPaddingRight();
            //换行
            if (left >= widthSize - getPaddingRight() - getPaddingLeft()) {
                left = getPaddingLeft();
                //累积行的总高度
                allHeight += maxHeightInLine + dip2px(verticalSpace);
                //因为换行了，所以每行的最大高度置0
                maxHeightInLine = 0;
            }

        }
        //再加上最后一行的高度,因为之前的高度累积条件是换行
        //最后一行没有换行操作，所以高度应该再加上
        allHeight += maxHeightInLine;

        if (widthMode != MeasureSpec.EXACTLY) {
            //如果没有指定宽，则默认为屏幕宽
            widthSize = mScreenWidth;
        }

        //如果没有指定高度
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = allHeight + getPaddingBottom() + getPaddingTop();
        }

        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            View child = null;
            //初始子view摆放的左上位置
            int left = getPaddingLeft();
            int top = getPaddingTop();
            //一行view中将最大的高度存于此变量，用于子view进行换行时高度的计算
            int maxHeightInLine = 0;
            for (int i = 0; i < getChildCount(); i++) {
                child = getChildAt(i);
                if (i > 0) {
                    //获取最大高度
                    if (getChildAt(i - 1).getMeasuredHeight() > maxHeightInLine) {
                        maxHeightInLine = getChildAt(i - 1).getMeasuredHeight();
                    }

                    //当前子view的起始left为 上一个子view的宽度+水平间距
                    left += getChildAt(i - 1).getMeasuredWidth() + dip2px(horizontalSpace);
                    //这一行所有子view相加的宽度大于容器的宽度，需要换行
                    if (left + child.getMeasuredWidth() >= getWidth() - getPaddingRight() - getPaddingLeft()) {
                        //换行的首个子view，起始left应该为0+容器的paddingLeft
                        left = getPaddingLeft();
                        //top的位置为上一行中拥有最大高度的某个View的高度+垂直间距
                        top += maxHeightInLine + dip2px(verticalSpace);
                        //将上一行View的最大高度置0
                        maxHeightInLine = 0;
                    }
                }
                //摆放子view
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }


    /**
     * dp转为px
     *
     * @param dpValue
     * @return
     */
    private int dip2px(float dpValue) {
        return (int) (dpValue * mDensity + 0.5f);
    }

    /**
     * 设置子view间的水平间距 单位dp
     *
     * @param horizontalSpace
     */
    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    /**
     * 设置子view间的垂直间距 单位dp
     *
     * @param verticalSpace
     */
    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

}
