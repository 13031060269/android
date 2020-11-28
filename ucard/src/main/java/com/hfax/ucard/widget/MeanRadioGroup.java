package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Created by liuweiping on 2018/7/27.
 */

public class MeanRadioGroup extends RadioGroup {
    public MeanRadioGroup(Context context) {
        this(context, null);
    }

    public MeanRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 复写布局方法
     *
     * @param changed 是否改变
     * @param l       左
     * @param t       上
     * @param r       右
     * @param b       下
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //获取到孩子的个数
        int childCount = getChildCount();
        int x = 0;

        //计算最大宽度
        int maxWidth = r - l;
        //间隙view之间
        int weight = 0;
        Rect rect=new Rect(0,0,0,getMeasuredHeight());
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (weight == 0) {
                    weight = (maxWidth - (width * childCount)) / (childCount + 1);
                }
                //计算XY
                x += width + weight;
                //设置孩子的位置
                child.layout(x - width, rect.centerY()-height/2, x, rect.centerY()+height/2);
            }
        }

    }

}
