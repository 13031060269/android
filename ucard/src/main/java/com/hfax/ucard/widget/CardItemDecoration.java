package com.hfax.ucard.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;

/**
 * 银行卡分割线
 *
 * @author SongGuangYao
 * @date 2018/5/15
 */

public class CardItemDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, orderStatus);
        outRect.bottom = Utils.dip2px(view.getContext(), 8);
    }
}
