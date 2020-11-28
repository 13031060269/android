package com.hfax.ucard.utils;

import android.content.Context;
import android.text.TextUtils;


import com.hfax.ucard.widget.HfaxRefreshViewHolder;

import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by eson on 2017/8/19.
 */

public class RefreshViewHolderFactory {

    public static BGARefreshViewHolder createRefreshViewHolder(Context context) {
        return createRefreshViewHolder(context, false);
    }

    public static BGARefreshViewHolder createRefreshViewHolder(Context context, boolean isEnableLoadMore) {
        return createRefreshViewHolder(context, isEnableLoadMore, null);
    }

    public static BGARefreshViewHolder createRefreshViewHolder(Context context, boolean isEnableLoadMore, String loadingMoreText) {
        return createRefreshViewHolder(context, isEnableLoadMore, loadingMoreText, null);
    }

    public static BGARefreshViewHolder createRefreshViewHolder(Context context, boolean isEnableLoadMore, String loadingMoreText, final OnRefreshViewChangeListener listener) {
        HfaxRefreshViewHolder refreshViewHolder = new HfaxRefreshViewHolder(context, isEnableLoadMore){
            @Override
            public void changeToIdle() {
                if (listener != null) {
                    listener.changeToIdle();
                }
            }

            @Override
            public void handleScale(float scale, int moveYDistance) {
                if (listener != null) {
                    listener.handleScale(scale, moveYDistance);
                }
            }
        };
        if (isEnableLoadMore) {
            if (TextUtils.isEmpty(loadingMoreText)) {
                loadingMoreText = "正在加载更多……";
            }
            refreshViewHolder.setLoadingMoreText(loadingMoreText);
        }
        return refreshViewHolder;
    }

    public interface OnRefreshViewChangeListener {

        public void changeToIdle();

        public void handleScale(float scale, int moveYDistance);
    }
}
