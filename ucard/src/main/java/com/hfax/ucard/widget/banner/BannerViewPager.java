package com.hfax.ucard.widget.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.hfax.lib.utils.Utils;

/**
 * 主页Viewpager
 * Created by SongGuangYao on 2018/11/5.
 */

public class BannerViewPager extends ViewPager {
    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Utils.getScreenWidth(getContext());
        //设置宽高比例
        setMeasuredDimension(width, (int) (width * 156.0f / 750));
    }
}
