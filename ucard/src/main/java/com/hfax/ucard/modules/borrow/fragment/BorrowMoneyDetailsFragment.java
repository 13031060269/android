package com.hfax.ucard.modules.borrow.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.BorrowBean;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.widget.PlanView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/8.
 */

public class BorrowMoneyDetailsFragment extends BorrowDetailsFragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_deadline)
    TextView tvDeadline;
    @BindView(R.id.tv_repayment)
    TextView tvRepayment;
    @BindView(R.id.tv_repayment_type)
    TextView tvRepaymentType;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.view_plan)
    PlanView viewPlan;
    @BindView(R.id.tv_center_text)
    TextView tv_center_text;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_more)
    TextView tv_more;
    @BindView(R.id.tv_money_text)
    TextView tv_money_text;
    @BindView(R.id.iv_state)
    ImageView iv_state;
    BorrowDetails mBorrowDetails;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_borrow_money_details;
    }

    @Override
    public void initData() {
        mBorrowDetails = (BorrowDetails) getArguments().getSerializable(BorrowDetailsActivity.KEY_DATA);
        if (mBorrowDetails == null) {
            showErrorView();
            return;
        }
        tvTitle.setText("借款详情");
        viewPlan.setVisibility(View.GONE);
        tv_phone.setVisibility(View.GONE);
        tv_more.setVisibility(View.GONE);
        tvMoney.setText(UCardUtil.formatAmount(mBorrowDetails.loanAmount));
        switch (mBorrowDetails.orderStatus) {
            case BorrowBean.STATE_CHECKING:
                tv_center_text.setText("您的借款申请正在审核中，审核通过将以短信方式通知");
                iv_state.setBackgroundResource(R.drawable.icon_state_checking);
                tv_money_text.setText("申请金额(元)");
                tvMoney.setText(UCardUtil.formatAmount(mBorrowDetails.applyAmount));
                break;
            case BorrowBean.STATE_PAYING:
                tv_center_text.setText("您的借款已确认，正在打款中，请耐心等待");
                iv_state.setBackgroundResource(R.drawable.icon_state_paying);
                break;
            case BorrowBean.STATE_NOPASS:
                tv_center_text.setText("您的借款申请未满足借款审批条件，暂不能申请借款");
                if(UCardUtil.isGoneDrainage()){
                    tv_more.setVisibility(View.GONE);
                }else{
                    tv_more.setVisibility(View.VISIBLE);
                }
                tv_more.setText("了解更多借款方式");
                iv_state.setBackgroundResource(R.drawable.icon_state_nopass);
                tv_money_text.setText("申请金额(元)");
                tvMoney.setText(UCardUtil.formatAmount(mBorrowDetails.applyAmount));
                break;
            case BorrowBean.STATE_TIMEOUT:
                tv_center_text.setText("您的借款申请72小时内未确认，已失效，请重新提交申请");
                tv_more.setVisibility(View.VISIBLE);
                tv_more.setText("重新申请");
                iv_state.setBackgroundResource(R.drawable.icon_state_timeout);
                break;
            default:
            case BorrowBean.STATE_FAIL:
                tv_center_text.setText("您的借款打款失败，详情请联系客服");
                tv_phone.setVisibility(View.VISIBLE);
                iv_state.setBackgroundResource(R.drawable.icon_state_fail);
                break;
        }
        tvDate.setText(mBorrowDetails.applyDate);
        if(mBorrowDetails.orderStatus==BorrowBean.STATE_TIMEOUT){
            tvRepayment.setText(UCardUtil.getLowAmount(mBorrowDetails.repayMonth,mBorrowDetails.loanAmount,mBorrowDetails.periods));
        }else{
            tvRepayment.setText(UCardUtil.formatAmount(mBorrowDetails.repayMonth));
        }
        tvDeadline.setText(mBorrowDetails.periods + "个月");
        tvRepaymentType.setText(mBorrowDetails.repayTypeName);
        if (UCardUtil.isCollectionEmpty(mBorrowDetails.orderInfoVos)) return;
        viewPlan.setPlan(mBorrowDetails.orderInfoVos);
    }

    @OnClick({R.id.iv_title_return, R.id.tv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                getActivity().finish();
                break;
            case R.id.tv_more:
                switch (mBorrowDetails.orderStatus) {
                    case BorrowBean.STATE_TIMEOUT:
                        MainActivity.start(getActivity());
                        break;
                    case BorrowBean.STATE_NOPASS:
                        H5Activity.startActivity(getActivity(),UCardUtil.getH5Url(NetworkAddress.H5_MORE_BORROW_MONEY_WAYS+"/mine"));
                        break;
                }
                break;
        }
    }

    @Override
    public void borrowChange(BorrowDetails borrowDetails) {

    }
}