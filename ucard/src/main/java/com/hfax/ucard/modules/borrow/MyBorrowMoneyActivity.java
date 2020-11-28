package com.hfax.ucard.modules.borrow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BorrowBean;
import com.hfax.ucard.bean.BorrowBeanList;
import com.hfax.ucard.modules.borrow.adapter.BorrowListAdapter;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.RefreshViewHolderFactory;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;


/**
 * Created by liuweiping on 2018/5/3.
 */

public class MyBorrowMoneyActivity extends BaseNetworkActivity<BorrowBeanList> implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_empty)
    View tv_empty;
    @BindView(R.id.iv_go_more_borrow)
    View iv_go_more_borrow;
    @BindView(R.id.lv_borrow)
    ListView lv_borrow;
    @BindView(R.id.refresh_layout)
    BGARefreshLayout bgaRefreshLayout;
    List<BorrowBean> borrowBeans = new ArrayList<>();
    BorrowListAdapter adapter = new BorrowListAdapter(borrowBeans);

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_borrow_money;
    }

    @Override
    public void initData() {
        tv_empty.setVisibility(View.GONE);
        tv_title.setText("我的借款");
        lv_borrow.setAdapter(adapter);
        bgaRefreshLayout.setDelegate(this);
        bgaRefreshLayout.setRefreshViewHolder(RefreshViewHolderFactory.createRefreshViewHolder(this));
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyBorrowMoneyActivity.class);
        UCardUtil.startActivity(context, intent);
    }

    @OnClick({R.id.iv_title_return, R.id.iv_go_more_borrow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.iv_go_more_borrow:
                H5Activity.startActivity(this, UCardUtil.getH5Url(NetworkAddress.H5_MORE_BORROW_MONEY_WAYS + "/loanlist"));
                break;
        }
    }

    @Override
    public void onSuccess(BorrowBeanList borrowBean) {
        dismissLoadingDialog();
        showContentView();
        bgaRefreshLayout.endRefreshing();
        if (borrowBean != null && !UCardUtil.isCollectionEmpty(borrowBean.items)) {
            tv_empty.setVisibility(View.GONE);
            borrowBeans.clear();
            borrowBeans.addAll(borrowBean.items);
            adapter.notifyDataSetChanged();
        } else {
            tv_empty.setVisibility(View.VISIBLE);
        }
        boolean show = false;
        for (BorrowBean item : borrowBean.items) {
            if (item != null && item.orderStatus == BorrowBean.STATE_REPAYMENT) {
                show = true;
            }
        }
        if (!UCardUtil.isGoneDrainage() && show) {
            iv_go_more_borrow.setVisibility(View.VISIBLE);
        } else {
            iv_go_more_borrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        bgaRefreshLayout.endRefreshing();
        dismissLoadingDialog();
        showErrorView();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.QUERY_LOAN_RECORD, MVPUtils.Method.POST);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        onLoad();
        dismissLoadingDialog();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
