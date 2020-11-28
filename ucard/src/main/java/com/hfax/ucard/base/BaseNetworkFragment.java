package com.hfax.ucard.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfax.app.BaseActivity;
import com.hfax.app.BaseFragment;
import com.hfax.ucard.R;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleNetworkAdapter;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleView;

public abstract class BaseNetworkFragment<T> extends BaseFragment implements SimpleView<T> {
    protected SimpleNetworkAdapter mNetworkAdapter = new SimpleNetworkAdapter(this);
    protected Activity activity;
    protected BaseActivity baseActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View iv_title_return = view.findViewById(R.id.iv_title_return);
        if (iv_title_return != null) {
            iv_title_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        if (activity instanceof BaseActivity) {
            this.baseActivity = (BaseActivity) activity;
        }
    }

    protected void finish() {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNetworkAdapter.onDesDroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        try {
            super.setUserVisibleHint(isVisibleToUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onReLoad() {
        super.onReLoad();
        showContentView();
        onLoad();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }
}
