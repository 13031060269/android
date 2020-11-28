package com.hfax.ucard.modules.borrow.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.app.h5.H5DepositActivity;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.MyFragmentActivity;
import com.hfax.ucard.bean.BorrowBean;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.bean.PaymentDetailsBean;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.borrow.adapter.PaymentDetailsAdapter;
import com.hfax.ucard.modules.loan.StatusActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.RefreshViewHolderFactory;
import com.hfax.ucard.utils.RepetitionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.widget.CardChangeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import java.util.List;


/**
 * Created by liuweiping on 2018/5/3.
 */

public class PaymentDetailsFragment extends BorrowDetailsFragment<PaymentDetailsBean> implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tv_title_right;
    @BindView(R.id.lv_payment_details)
    ListView lvPaymentDetails;
    @BindView(R.id.tv_payment_money)
    TextView tvPaymentMoney;
    @BindView(R.id.tv_youhui)
    TextView tv_youhui;
    @BindView(R.id.tv_payment)
    TextView tvPayment;
    @BindView(R.id.bottom)
    View bottom;
    PaymentDetailsAdapter adapter = new PaymentDetailsAdapter();
    BorrowDetails.RepayPlanVo repayPlanVo;
    ViewHold viewHold;
    @BindView(R.id.refresh_layout)
    BGARefreshLayout refresh_layout;
    String applyNo;
    public List<BorrowDetails.Coupon> coupon;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_payment_details;
    }

    @Override
    public void initData() {
        GrowingIOUtils.track(UCardConstants.ANDR_PAYMENT_PAGE);
        final BorrowDetails mBorrowDetails = (BorrowDetails) getArguments().getSerializable(BorrowDetailsActivity.KEY_DATA);
        if (mBorrowDetails == null) {
            showErrorView();
            return;
        }
        applyNo = mBorrowDetails.applyNo;
        tvTitle.setText("还款详情");
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("查看合同");
        tv_title_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_payment_details, lvPaymentDetails, false);
        lvPaymentDetails.addHeaderView(headView);
        View foot = new View(getActivity());
        foot.setLayoutParams(new AbsListView.LayoutParams(1, Utils.dip2px(BaseApplication.getContext(), 10)));
        lvPaymentDetails.addFooterView(foot);
        lvPaymentDetails.setAdapter(adapter);
        viewHold = new ViewHold(headView);
        refresh_layout.setDelegate(this);
        refresh_layout.setRefreshViewHolder(RefreshViewHolderFactory.createRefreshViewHolder(getActivity()));
        borrowChange(mBorrowDetails);
        headView.findViewById(R.id.tv_change_bank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowingIOUtils.track(UCardConstants.ANDR_PAYMENT_C_CLICK);
                CardChangeDialog.checkBank(PaymentDetailsFragment.this, mBorrowDetails, new ActivityCallbackUtils.Callback() {
                    @Override
                    public void callback(Object o) {
                        if ((o instanceof Boolean) && (Boolean) o) {
                            showToast("换卡提交成功，请稍后刷新页面");
                            setNeedRefresh(false);
                        }
                    }
                });
            }
        });
        TextView tv_plan_describe = headView.findViewById(R.id.tv_plan_describe);
        if (UCardUtil.isEmpty(mBorrowDetails.repayNoticeMsg)) {
            tv_plan_describe.setVisibility(View.GONE);
        } else {
            tv_plan_describe.setText(mBorrowDetails.repayNoticeMsg);
            tv_plan_describe.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void borrowChange(BorrowDetails mBorrowDetails) {
        refresh_layout.endRefreshing();
        if (mBorrowDetails == null) {
            return;
        }
        onCheck(false, null);
        viewHold.update(mBorrowDetails);
        this.coupon = mBorrowDetails.coupon;
        if (!UCardUtil.isCollectionEmpty(mBorrowDetails.repayPlanVos)) {
            adapter.resetDate(mBorrowDetails.repayPlanVos, coupon);
            for (BorrowDetails.RepayPlanVo repayPlanVo : mBorrowDetails.repayPlanVos) {
                if (repayPlanVo.selective) {
                    onCheck(true, repayPlanVo);
                    break;
                }
            }
        }
    }

    @Override
    public void noRefresh() {
        setNeedRefresh(true);
    }

    public void onCheck(boolean isCheck, BorrowDetails.RepayPlanVo repayPlanVo) {
        PaymentDetailsFragment.this.repayPlanVo = repayPlanVo;
        tvPayment.setEnabled(isCheck);
        if (isCheck) {
            tvPaymentMoney.setTextColor(0xffF69C4F);
            tvPaymentMoney.setText(UCardUtil.formatAmount(repayPlanVo.getRepayTotal()));
            if (!UCardUtil.isCollectionEmpty(coupon)) {
                tv_youhui.setVisibility(View.VISIBLE);
                tv_youhui.setText("息费可优惠" + UCardUtil.formatAmount(coupon.get(0).derateAmount));
            } else if (repayPlanVo.deRateTotal > 0) {
                tv_youhui.setVisibility(View.VISIBLE);
                tv_youhui.setText("罚息已减免" + UCardUtil.formatAmount(repayPlanVo.deRateTotal));
            }
        } else {
            tvPaymentMoney.setText("0.00");
            tvPaymentMoney.setTextColor(0xff3A3C48);
            tv_youhui.setVisibility(View.GONE);
        }
    }

    public static void start(Context context) {
        UCardUtil.startActivity(context, new Intent(context, PaymentDetailsFragment.class));
    }

    @OnClick({R.id.iv_title_return, R.id.tv_title_right, R.id.tv_payment})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                getActivity().finish();
                break;
            case R.id.tv_payment://确认还款
                GrowingIOUtils.track(UCardConstants.ANDR_PAYMENT_CARD_CLICK);
                if (repayPlanVo == null) return;
                final AlertDialog dialog;
                final View root;
                if (repayPlanVo.overdueTotal > 0 && repayPlanVo.deRateTotal > 0) {
                    root = getLayoutInflater().inflate(R.layout.dialog_confirm_payment_details_2, null);
                    dialog = new AlertDialog.Builder(getActivity()).setView(root).create();
                    TextView tv_value_2 = root.findViewById(R.id.tv_value_2);
                    tv_value_2.setText(UCardUtil.formatAmount(repayPlanVo.deRateTotal) + "元");
                } else if (!UCardUtil.isCollectionEmpty(coupon)) {
                    root = getLayoutInflater().inflate(R.layout.dialog_confirm_payment_details_2, null);
                    dialog = new AlertDialog.Builder(getActivity()).setView(root).create();
                    TextView tv_name_2 = root.findViewById(R.id.tv_name_2);
                    TextView tv_value_2 = root.findViewById(R.id.tv_value_2);
                    tv_name_2.setText("红包优惠：");
                    tv_value_2.setText(UCardUtil.formatAmount(coupon.get(0).derateAmount) + "元(可用于减免息费)");
                } else {
                    root = getLayoutInflater().inflate(R.layout.dialog_confirm_payment_details, null);
                    dialog = new AlertDialog.Builder(getActivity()).setView(root).create();
                }
                TextView tv_value_1 = root.findViewById(R.id.tv_value_1);
                tv_value_1.setText(UCardUtil.formatAmount(repayPlanVo.getRepayTotal()) + "元");
                root.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                root.findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        submit();
                    }
                });
                dialog.show();
                break;
            case R.id.tv_title_right://查看合同
                H5Activity.startActivity(getActivity(), String.format(UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT), applyNo, ""));
                break;
        }
    }

    void submit() {
        showLoadingDialog();
        final RequestMap requestMap = new RequestMap(NetworkAddress.REPAY);
        requestMap.put("applyNo", applyNo);
        requestMap.put("repayInfos", repayPlanVo.getPostParameter(coupon));
        RepetitionUtils.getRepetition().submit(requestMap, getActivity(), new DataChange<Boolean>() {
            @Override
            public void onChange(Boolean aBoolean) {
                if (aBoolean) {
                    mNetworkAdapter.request(requestMap, MVPUtils.Method.POST);
                } else {
                    dismissLoadingDialog();
                }
            }
        });
    }

    @Override
    public void onSuccess(PaymentDetailsBean paymentDetailsBean) {
        StatusActivity.start(getActivity(), UCardConstants.STATUS_BACK);
        dismissLoadingDialog();
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
        switch (code) {
            case NetworkAddress.SUBMIT_TOKEN_ERROR:
            case NetworkAddress.UNIDENTIFIED_ERROR:
            case NetworkAddress.DEFAULT2:
            case NetworkAddress.REPAY_STATUS_ERROR:
                onBGARefreshLayoutBeginRefreshing(null);
                break;
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DataChange) {
            ((DataChange) activity).onChange(refreshLayout);
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    public class ViewHold {
        @BindView(R.id.top)
        View top;
        @BindView(R.id.iv_state)
        View iv_state;
        @BindView(R.id.tv_withdrawing)
        TextView tv_withdrawing;
        @BindView(R.id.tv_withdraw)
        TextView tv_withdraw;
        @BindView(R.id.tv_amountToBeWithdrawn)
        TextView tv_amountToBeWithdrawn;
        @BindView(R.id.tv_should)
        TextView tv_should;
        @BindView(R.id.tv_loanAmount)
        TextView tv_loanAmount;
        @BindView(R.id.tv_periods)
        TextView tv_periods;
        @BindView(R.id.iv_bank_logo)
        ImageView iv_bank_logo;
        @BindView(R.id.tv_bank_name)
        TextView tv_bank_name;
        @BindView(R.id.tv_bank_num)
        TextView tv_bank_num;
        @BindView(R.id.tv_surplus_periods)
        TextView tv_surplus_periods;

        @BindView(R.id.tv_status_name)
        TextView tv_status_name;
        @BindView(R.id.tv_notice)
        TextView tv_notice;
        @BindView(R.id.ll_notice)
        View ll_notice;
        @BindView(R.id.tv_change_bank)
        View tv_change_bank;

        ViewHold(View headView) {
            ButterKnife.bind(this, headView);
            top.setVisibility(View.GONE);
        }

        void update(final BorrowDetails mBorrowDetails) {
            mBorrowDetails.initRepayCardVo();
            if (mBorrowDetails.failBarContentVo != null) {
                ll_notice.setVisibility(View.VISIBLE);
                ll_notice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestMap requestMap = new RequestMap(NetworkAddress.RECORD_CLICK_FAIL_BAR);
                        requestMap.put("applyNo", mBorrowDetails.applyNo);
                        mNetworkAdapter.request(requestMap, MVPUtils.Method.POST, null);
                        BorrowDetails details = new BorrowDetails();
                        details.orderStatus = BorrowBean.STATE_FAIL;
                        details.loanAmount = mBorrowDetails.failBarContentVo.loanAmount;
                        details.applyDate = mBorrowDetails.failBarContentVo.applyDate;
                        details.periods = mBorrowDetails.failBarContentVo.periods;
                        details.repayMonth = mBorrowDetails.failBarContentVo.repayMonth;
                        details.repayTypeName = mBorrowDetails.repayTypeName;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BorrowDetailsActivity.KEY_DATA, details);
                        MyFragmentActivity.start(getActivity(), BorrowMoneyDetailsFragment.class, bundle);
                    }
                });
            } else {
                ll_notice.setVisibility(View.GONE);
            }
            tv_amountToBeWithdrawn.setText(UCardUtil.formatAmount(mBorrowDetails.amountToBeWithdrawn));
            tv_should.setText(UCardUtil.formatAmount(mBorrowDetails.surplusMonth));
            tv_loanAmount.setText(UCardUtil.formatAmount(mBorrowDetails.loanAmount));
            tv_surplus_periods.setText(mBorrowDetails.surplusPeriods + "");
            tv_periods.setText("（共" + mBorrowDetails.periods + "期）");
            tv_change_bank.setVisibility(View.VISIBLE);
            if (mBorrowDetails.repayCardVo != null) {
                tv_bank_name.setText(mBorrowDetails.repayCardVo.bankName);
                tv_bank_num.setText("尾号" + UCardUtil.getBankNumLast(mBorrowDetails.repayCardVo.cardNo));
                GlideUtils.requestImageCode(getActivity(), UCardUtil.getBankCardLogo(mBorrowDetails.repayCardVo.bankCode, false), iv_bank_logo);
            }
            switch (mBorrowDetails.orderStatus) {
                case BorrowBean.STATE_REPAYMENT:
                    break;
                case BorrowBean.STATE_RETURN:
                    tv_change_bank.setVisibility(View.GONE);
                    iv_state.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.GONE);
                case BorrowBean.STATE_WITHDRAW:
                    if (mBorrowDetails.isWithdraw == 997) {
                        tv_withdrawing.setVisibility(View.GONE);
                        top.setVisibility(View.VISIBLE);
                        tv_withdraw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (PreventClickUtils.canNotClick(v)) return;
                                showLoadingDialog();
                                final RequestMap map = new RequestMap(NetworkAddress.WITHDRAW);
                                map.put("applyNo", mBorrowDetails.applyNo);
                                map.put("amountToBeWithdraw", mBorrowDetails.amountToBeWithdrawn);
                                RepetitionUtils.getRepetition().submit(map, PaymentDetailsFragment.this, new DataChange<Boolean>() {
                                    @Override
                                    public void onChange(Boolean aBoolean) {
                                        dismissLoadingDialog();
                                        if (aBoolean) {
                                            H5DepositActivity.startDepositActivity(getActivity(), Utils.getApiURL(map.getPath()), "", Utils.getRequestParams(map));
                                        }
                                    }
                                });
                            }
                        });
                    } else if (mBorrowDetails.isWithdraw == 998) {
                        tv_withdrawing.setVisibility(View.VISIBLE);
                        top.setVisibility(View.VISIBLE);
                    } else {
                        top.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }
}
