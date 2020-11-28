package com.hfax.ucard.widget.banner;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hfax.lib.utils.Utils;

/**
 * Created by SongGuangYao on 2018/11/5.
 */

public class BannerImageView extends AppCompatImageView {
    public BannerImageView(Context context) {
        super(context);
        init();
    }

    public BannerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
    }

    public BannerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Utils.getScreenWidth(getContext());
        //设置宽高比例
        setMeasuredDimension(width, (int) (width * 156.0f / 750));
    }

}
