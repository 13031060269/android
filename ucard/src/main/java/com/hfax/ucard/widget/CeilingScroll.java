package com.hfax.ucard.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hfax.ucard.R;

/**
 * @author lwp
 * 联动顶部标题的ScrollView
 */
public class CeilingScroll extends ScrollView {
    private TextView ceilingView, bigTV;
    private int height;

    public CeilingScroll(Context context) {
        this(context, null);
    }

    public CeilingScroll(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CeilingScroll(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (ceilingView == null) {
            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                ceilingView = ((ViewGroup) parent).findViewById(R.id.tv_title);
            }
            if (ceilingView == null) {
                return;
            }

            try {
                bigTV = (TextView) ((ViewGroup) getChildAt(0)).getChildAt(0);
                if (bigTV != null) {
                    bigTV.setText(ceilingView.getText());
                    ceilingView.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (ceilingView == null) {
            return;
        }
        if (bigTV == null) {
            return;
        }
        if (height == 0) {
            height = bigTV.getHeight() - bigTV.getPaddingBottom();
        }
        if (ceilingView != null) {
            if (t >= height) {
                ceilingView.setVisibility(VISIBLE);
            } else {
                ceilingView.setVisibility(INVISIBLE);
            }
        }
    }
}
