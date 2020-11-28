package com.hfax.ucard.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hfax.ucard.R;


/**
 * 加载git的imageview
 */
@SuppressLint("AppCompatCustomView")
public class GifImageView extends ImageView {
    public GifImageView(Context context) {
        this(context, null);

    }

    public GifImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifImageView);
        int resourceId = a.getResourceId(R.styleable.GifImageView_gif, 0);
        int diskCacheStrategy = a.getResourceId(R.styleable.GifImageView_diskCacheStrategy, 0);
        if (resourceId != 0) {
            DiskCacheStrategy disk;
            switch (diskCacheStrategy) {
                default:
                case 0:
                    disk = DiskCacheStrategy.ALL;
                    break;
                case 1:
                    disk = DiskCacheStrategy.NONE;
                    break;
                case 2:
                    disk = DiskCacheStrategy.SOURCE;
                    break;
                case 3:
                    disk = DiskCacheStrategy.RESULT;
                    break;
            }
            Glide.with(context).load(resourceId).asGif().diskCacheStrategy(disk).into(this);
        }
        a.recycle();
    }
}
