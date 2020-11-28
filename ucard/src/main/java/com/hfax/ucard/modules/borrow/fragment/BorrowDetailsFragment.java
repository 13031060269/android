package com.hfax.ucard.modules.borrow.fragment;

import com.hfax.ucard.base.BaseNetworkFragment;
import com.hfax.ucard.bean.BorrowDetails;

/**
 * Created by liuweiping on 2018/5/8.
 */

public abstract class BorrowDetailsFragment<T> extends BaseNetworkFragment<T> {
    public abstract void borrowChange(BorrowDetails borrowDetails);

    private boolean needRefresh = true;

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onFail(int code, String msg) {

    }

    @Override
    protected void initListener() {

    }

    public boolean needRefresh() {
        return needRefresh;
    }

    public void noRefresh() {
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }
}