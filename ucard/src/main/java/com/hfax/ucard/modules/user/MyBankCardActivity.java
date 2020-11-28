package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BankListBean;
import com.hfax.ucard.modules.user.fragment.CardListCreditFragment;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.widget.TabStrip;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class MyBankCardActivity extends BaseNetworkActivity<BankListBean> {
    @BindView(R.id.iv_title_return)
    ImageView ivTitleReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tab_strip)
    TabStrip tabStrip;
    @BindView(R.id.vp_bank_card)
    ViewPager vpBankCard;
    CardListCreditFragment mCardListCreditFragment = new CardListCreditFragment();
    CardListCreditFragment mCardListRepaymentFragment = CardListCreditFragment.create("收/还款银行卡","用于收款及偿还借款");
    final Fragment[] fs = {mCardListCreditFragment, mCardListRepaymentFragment};

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_mybank_card;
    }

    @Override
    public void initData() {
        tvTitle.setText("我的银行卡");
        vpBankCard.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fs[position];
            }

            @Override
            public int getCount() {
                return fs.length;
            }
        });
        tabStrip.setViewPager(vpBankCard);
    }

    protected void onLoad() {
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.QUERY_BANK_LIST, MVPUtils.Method.GET);
    }

    public static void start(Context context) {
        UCardUtil.startActivity(context, new Intent(context, MyBankCardActivity.class));
    }

    @OnClick({R.id.iv_title_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(BankListBean bankListBean) {
        dismissLoadingDialog();
        showContentView();
        mCardListCreditFragment.onChange(bankListBean.creditCardList);
        mCardListRepaymentFragment.onChange(bankListBean.platformCardList);
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showErrorView();
    }
}
