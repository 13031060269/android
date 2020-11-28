package com.hfax.ucard.modules.borrow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BorrowBean;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.modules.borrow.fragment.AffirmNeedBorrowFragment;
import com.hfax.ucard.modules.borrow.fragment.BorrowDetailsFragment;
import com.hfax.ucard.modules.borrow.fragment.BorrowMoneyDetailsFragment;
import com.hfax.ucard.modules.borrow.fragment.PaymentDetailsFragment;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;

/**
 * Created by liuweiping on 2018/6/7.
 */

public class BorrowDetailsActivity extends BaseNetworkActivity<BorrowDetails> implements DataChange {
    FrameLayout rootView;
    public static String APPLY_NO = "applyNo";
    public String applyNo;
    public static String KEY_DATA = "borrowDetails";
    BorrowDetailsFragment curFragment;

    @Override
    public void onSuccess(BorrowDetails borrowDetails) {
        if (borrowDetails == null) {
            showErrorView();
            dismissLoadingDialog();
            return;
        }
        BorrowDetailsFragment fragment = null;
        switch (borrowDetails.orderStatus) {
            default:
            case BorrowBean.STATE_CHECKING:
            case BorrowBean.STATE_PAYING:
            case BorrowBean.STATE_NOPASS:
            case BorrowBean.STATE_TIMEOUT:
            case BorrowBean.STATE_FAIL:
//                if (curFragment instanceof BorrowMoneyDetailsFragment) {
//                    curFragment.borrowChange(borrowDetails);
//                } else {
                fragment = new BorrowMoneyDetailsFragment();
//                }
                break;
            case BorrowBean.STATE_REPAYMENT:
            case BorrowBean.STATE_WITHDRAW:
            case BorrowBean.STATE_RETURN:
                if (curFragment instanceof PaymentDetailsFragment) {
                    curFragment.borrowChange(borrowDetails);
                } else {
                    fragment = new PaymentDetailsFragment();
                }
                break;
            case BorrowBean.STATE_AFFIRM_NEED:
                if (curFragment instanceof AffirmNeedBorrowFragment) {
                    curFragment.borrowChange(borrowDetails);
                } else {
                    fragment = new AffirmNeedBorrowFragment();
                }
                break;
        }
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_DATA, borrowDetails);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(rootView.getId(), fragment).commit();
            curFragment = fragment;
        }
        showContentView();
        dismissLoadingDialog();
    }

    @Override
    public void onFail(int code, String msg) {
        if (curFragment != null) {
            showToast(msg);
            curFragment.borrowChange(null);
        }
        showErrorView();
        dismissLoadingDialog();
    }

    public static void start(Context context, String applyNo) {
        Intent intent = new Intent(context, BorrowDetailsActivity.class).putExtra(APPLY_NO, applyNo);
        UCardUtil.startActivity(context, intent);
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    public void initListener() {
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (curFragment != null && !curFragment.needRefresh()) {
            curFragment.noRefresh();
            return;
        }
        showLoadingDialog();
        RequestMap map = new RequestMap(NetworkAddress.QUERY_LOAN_DETAIL);
        map.put("applyNo", applyNo);
        mNetworkAdapter.request(map, MVPUtils.Method.POST);
    }

    @Override
    public void initData() {
        applyNo = getIntent().getStringExtra(APPLY_NO);
    }

    @Override
    protected View getLayoutView() {
        rootView = new FrameLayout(this);
        rootView.setId(UCardUtil.generateViewId());
        return rootView;
    }

    @Override
    public void onChange(Object o) {
        onLoad();
        if(o!=null){
            dismissLoadingDialog();
        }
    }
}
