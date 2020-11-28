package com.hfax.ucard.modules.user.fragment;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.lib.AppConfig;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkFragment;
import com.hfax.ucard.base.MyFragmentActivity;
import com.hfax.ucard.bean.CacheBean;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.bean.UserBean;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.borrow.MyBorrowMoneyActivity;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.loan.CertificationActivity;
import com.hfax.ucard.modules.user.CouponActivity;
import com.hfax.ucard.modules.user.PersonalCenterActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by liuweiping on 2018/4/3.
 */

public class UserFragment extends BaseNetworkFragment<UserBean> {
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_payment_total)
    TextView tv_payment_total;
    @BindView(R.id.tv_payment_month)
    TextView tv_payment_month;
    @BindView(R.id.tv_status1)
    TextView tv_status1;
    @BindView(R.id.tv_status2)
    TextView tv_status2;
    @BindView(R.id.ll_notice)
    View ll_notice;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_user;
    }

    @OnClick({R.id.iv_personal_center, R.id.iv_navigator, R.id.go_borrow, R.id.ll_feedback, R.id.ll_service, R.id.ll_about, R.id.go_coupon, R.id.go_msg})
    public void onClick(View v) {
        if (PreventClickUtils.canNotClick(v)) return;
        switch (v.getId()) {
            case R.id.go_borrow:
                MyBorrowMoneyActivity.start(getActivity());
                break;
            case R.id.go_coupon:
                startActivity(new Intent(activity, CouponActivity.class));
                break;
            case R.id.go_msg:
                MyFragmentActivity.start(getActivity(),MsgCenterFragment.class,null);
                break;
            case R.id.ll_feedback:
                H5Activity.startActivity(getActivity(), UCardUtil.getH5Url(NetworkAddress.H5_FAQ));
                break;
            case R.id.ll_service:
                GrowingIOUtils.track(UCardConstants.ANDR_ONLINE_CUSTSERVICE_CLICK);
                if (AppConfig.IMUtil != null) {
                    AppConfig.IMUtil.startChat();
                }
                break;
            case R.id.iv_navigator:
            case R.id.iv_personal_center:
                PersonalCenterActivity.start(getActivity());
                break;
            case R.id.ll_about:
                H5Activity.startActivity(getActivity(), UCardUtil.getH5Url(NetworkAddress.ABOUT_US), "关于我们");
                break;
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (!UserModel.isLogin()) {
            MainActivity.start(getActivity());
        } else {
            UserBean cache = CacheBean.getCache(UserBean.class);
            if (cache != null) {
                onSuccess(cache);
            } else {
                tv_phone.setText("");
                tv_payment_total.setText("--");
                tv_payment_month.setText("--");
                ll_notice.setVisibility(View.GONE);
            }
            showLoadingDialog();
            mNetworkAdapter.request(NetworkAddress.QUERY_SUMMARY, MVPUtils.Method.GET);
        }
    }

    @Override
    public void onSuccess(final UserBean userBean) {
        dismissLoadingDialog();
        userBean.saveCache();
        if (userBean == null) return;
        tv_phone.setText(LoginBean.getMobile());
        if (userBean.repaymentSummary != null) {
            tv_payment_total.setText(UCardUtil.formatAmount2(userBean.repaymentSummary.paymentTotal));
            tv_payment_month.setText(UCardUtil.formatAmount2(userBean.repaymentSummary.paymentMonth));
        }
        final UserBean.NoticeBean notice = userBean.notice;
        if (notice == null) return;
        switch (notice.status) {
            default:
            case 0:
                ll_notice.setVisibility(View.GONE);
                break;
            case 1:
                ll_notice.setVisibility(View.VISIBLE);
                tv_status1.setVisibility(View.VISIBLE);
                tv_status1.setText(notice.notice);
                tv_status2.setVisibility(View.GONE);
                ll_notice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CertificationActivity.start(getActivity(), UCardConstants.IDCARD_PERSON_CENTEL);
                    }
                });
                break;
            case 2:
                if (TextUtils.isEmpty(notice.notice)) return;
                ll_notice.setVisibility(View.VISIBLE);
                tv_status1.setVisibility(View.GONE);
                tv_status2.setVisibility(View.VISIBLE);
                tv_status2.setText(notice.notice);
                if (TextUtils.isEmpty(notice.applyNo)) return;
                ll_notice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BorrowDetailsActivity.start(getActivity(), notice.applyNo);
                    }
                });
                break;
        }

    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }
}
