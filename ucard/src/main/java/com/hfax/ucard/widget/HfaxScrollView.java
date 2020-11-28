package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by eson on 2017/7/18.
 */

public class HfaxScrollView extends NestedScrollView {

    private OnScrollChangeListener mListener;

    public HfaxScrollView(Context context) {
        super(context);
    }

    public HfaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HfaxScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setOnScallChangedListener(OnScrollChangeListener listener) {
        mListener = listener;
    }

    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v          The view whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
