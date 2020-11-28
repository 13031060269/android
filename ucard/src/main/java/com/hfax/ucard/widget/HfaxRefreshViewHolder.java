package com.hfax.ucard.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.hfax.ucard.R;

import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by eson on 2017/8/16.
 */

public class HfaxRefreshViewHolder extends BGARefreshViewHolder {
    private TextView mHeaderStatusTv;
    private ImageView mHeaderCircle1Iv;
    private ImageView mHeaderCircle2Iv;

    private AnimationDrawable animationDrawable;

    private String mPullDownRefreshText = "下拉刷新";
    private String mReleaseRefreshText = "释放更新";
    private String mRefreshingText = "加载中...";

    private int duration = 800;

    /**
     * @param context
     * @param isLoadingMoreEnabled 上拉加载更多是否可用
     */
    public HfaxRefreshViewHolder(Context context, boolean isLoadingMoreEnabled) {
        super(context, isLoadingMoreEnabled);
        setPullDistanceScale(2.0f);
    }

    /**
     * 设置未满足刷新条件，提示继续往下拉的文本
     *
     * @param pullDownRefreshText
     */
    public void setPullDownRefreshText(String pullDownRefreshText) {
        mPullDownRefreshText = pullDownRefreshText;
    }

    /**
     * 设置满足刷新条件时的文本
     *
     * @param releaseRefreshText
     */
    public void setReleaseRefreshText(String releaseRefreshText) {
        mReleaseRefreshText = releaseRefreshText;
    }

    /**
     * 设置正在刷新时的文本
     *
     * @param refreshingText
     */
    public void setRefreshingText(String refreshingText) {
        mRefreshingText = refreshingText;
    }

    @Override
    public View getRefreshHeaderView() {
        if (mRefreshHeaderView == null) {
            mRefreshHeaderView = View.inflate(mContext, R.layout.refresh_header_view, null);
            mRefreshHeaderView.setBackgroundColor(Color.TRANSPARENT);
            if (mRefreshViewBackgroundColorRes != -1) {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundColorRes);
            }
            if (mRefreshViewBackgroundDrawableRes != -1) {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundDrawableRes);
            }
            mHeaderStatusTv = (TextView) mRefreshHeaderView.findViewById(R.id.text);
            mHeaderCircle1Iv = (ImageView) mRefreshHeaderView.findViewById(R.id.header_img);
            mHeaderCircle2Iv = (ImageView) mRefreshHeaderView.findViewById(R.id.header_anim);
            animationDrawable = (AnimationDrawable) mHeaderCircle2Iv.getDrawable();
            mHeaderCircle2Iv.setVisibility(View.INVISIBLE);
            mHeaderStatusTv.setText(mPullDownRefreshText);
            mHeaderStatusTv.setVisibility(View.GONE);
        }
        return mRefreshHeaderView;
    }

    @Override
    public void handleScale(float scale, int moveYDistance) {

    }

    @Override
    public void changeToIdle() {
    }

    @Override
    public void changeToPullDown() {
        mHeaderStatusTv.setText(mPullDownRefreshText);
        mHeaderCircle2Iv.setVisibility(View.INVISIBLE);
        mHeaderCircle1Iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void changeToReleaseRefresh() {
        mHeaderStatusTv.setText(mReleaseRefreshText);
    }

    @Override
    public void changeToRefreshing() {
        mHeaderStatusTv.setText(mRefreshingText);
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable.start();
        mHeaderCircle1Iv.setVisibility(View.INVISIBLE);
        mHeaderCircle2Iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEndRefreshing() {
        mHeaderStatusTv.setText(mPullDownRefreshText);
        mHeaderCircle1Iv.setVisibility(View.VISIBLE);
        mHeaderCircle2Iv.setVisibility(View.INVISIBLE);
        animationDrawable.stop();
    }
}
