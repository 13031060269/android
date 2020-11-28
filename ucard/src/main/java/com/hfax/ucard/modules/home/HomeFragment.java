package com.hfax.ucard.modules.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.app.widget.HfaxDialog;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.ActivityCallbackUtils.Callback;
import com.hfax.lib.utils.Utils;
import com.hfax.lib.widget.indIcator.CirclePageIndicator;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkFragment;
import com.hfax.ucard.bean.*;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.loan.CertificationActivity;
import com.hfax.ucard.modules.loan.FaceActivity;
import com.hfax.ucard.modules.user.LoginActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GlobalConfigUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.CeilingScroll;
import com.hfax.ucard.widget.CusEditView;
import com.hfax.ucard.widget.PicketDialog;
import com.hfax.ucard.widget.banner.BannerAdapter;
import com.hfax.ucard.widget.banner.BannerViewPager;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by liuweiping on 2018/4/3.
 */

public class HomeFragment extends BaseNetworkFragment<HomeBean> {
    @BindView(R.id.ll_notice_root)
    View ll_notice;
    @BindView(R.id.line_et)
    View line_et;
    @BindView(R.id.ll_root_month)
    View ll_root_month;
    @BindView(R.id.tv_status_name)
    TextView tv_status_name;
    @BindView(R.id.tv_month)
    TextView tv_month;
    @BindView(R.id.tv_notice)
    TextView tv_notice;
    @BindView(R.id.tv_borrowing_balance)
    CusEditView tvBorrowingBalance;
    @BindView(R.id.hvp_viewpager)
    BannerViewPager hvpViewpager;
    @BindView(R.id.cpi_indicator)
    CirclePageIndicator cpiIndicator;
    @BindView(R.id.rl_banner)
    RelativeLayout rlBanner;
    @BindView(R.id.tv_use)
    TextView tv_use;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;
    private boolean mIsSoftKeyboardShowing;
    private int screenHeight;
    @BindView(R.id.tv_query_trial_repayment)
    TextView tv_query_trial_repayment;
    @BindView(R.id.bt_color_goto_borrow_money)
    View bt_color_goto_borrow_money;
    private int period = 6;
    private long amount;
    private long max;
    private long min;
    private boolean requestSucces;
    //Banner实体
    private ArrayList<BannerBean> mBannerList = new ArrayList<>();
    private BannerAdapter mBannerAdapter;
    private int BANNER_INTERVAL = 3000;
    private boolean isVisible;

    /**
     * banner轮播
     */
    Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isVisible && mBannerList.size() > 0) {
                int currentPosition = hvpViewpager.getCurrentItem() + 1;
                hvpViewpager.setCurrentItem(currentPosition);
                mHandler.postDelayed(this, BANNER_INTERVAL);
            }
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        tvBorrowingBalance.setLongClickable(false);
        tvBorrowingBalance.setTextIsSelectable(false);

        GlobalConfigUtils.request(mNetworkAdapter);
        ll_notice.setVisibility(View.GONE);
        final ViewGroup decorView = (ViewGroup) getActivity().getWindow().getDecorView();
        final View head = LayoutInflater.from(getActivity()).inflate(R.layout.hint_home_softchange, decorView, false);
        final TextView text = head.findViewById(R.id.tv_hint_home);
        mIsSoftKeyboardShowing = false;
        mLayoutChangeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (screenHeight == 0) {
                    screenHeight = Utils.getScreenHeight(getActivity());
                }
                //判断窗口可见区域大小
                Rect r = new Rect();
                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
                int heightDifference = screenHeight - (r.bottom - r.top);
                boolean isKeyboardShowing = heightDifference > screenHeight / 3;
                if (mIsSoftKeyboardShowing && !isKeyboardShowing) {//关闭软键盘
                    decorView.removeView(head);
                    int num = getBorrowMoney(tvBorrowingBalance.getText().toString());
                    setRepaymentText(num);
                    query_trial_repayment(null);
                } else if (!mIsSoftKeyboardShowing && isKeyboardShowing) {//打开软键盘
                    text.setText(String.format("金额范围%d-%d，1000的整数倍", min, max));
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) head.getLayoutParams();
                    lp.topMargin = r.bottom - lp.height;
                    decorView.addView(head, lp);
                }
                mIsSoftKeyboardShowing = isKeyboardShowing;
            }
        };
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
        //注册布局变化监听
        loadBanner();
        SensorsDataAPI.sharedInstance().ignoreView(hvpViewpager);
        ll_root_month.setVisibility(View.GONE);
    }

    public void refresh() {
        GlobalConfigBean globalConfig = GlobalConfigUtils.getGlobalConfig();
        if (globalConfig != null && globalConfig.getHomeConfig() != null) {
            GlobalConfigBean.HomeConfig homeConfig = globalConfig.getHomeConfig();
//            amount = homeConfig.defaultAmount;
            max = homeConfig.maxAmount / 100;
            min = homeConfig.minAmount / 100;
            tvBorrowingBalance.setHint("最高借" + max + "元");
        }
    }

    @Override
    protected void initListener() {
        hvpViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopScrollBanner();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        startScrollBanner();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        query_loan_status();
        query_trial_repayment(null);
        queryBanners();
        refresh();
    }

    @Override
    protected void onUserLogout() {
        ll_notice.setVisibility(View.GONE);
    }

    void query_loan_status() {//借款申请进度查询(首页订单状态提示条)
        if (UserModel.isLogin()) {
            mNetworkAdapter.request(NetworkAddress.QUERY_LOAN_STATUS, MVPUtils.Method.GET, new SimpleViewImpl<LoanStatus4BarBean>() {
                @Override
                public void onSuccess(final LoanStatus4BarBean loanStatus4Bar) {
                    if (loanStatus4Bar == null) return;
                    if (!TextUtils.isEmpty(loanStatus4Bar.notice)) {
                        tv_status_name.setText(loanStatus4Bar.notice);
                        tv_notice.setText(loanStatus4Bar.actionNotice);
                        ll_notice.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(loanStatus4Bar.applyNo)) return;
                        ll_notice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BorrowDetailsActivity.start(getActivity(), loanStatus4Bar.applyNo);
                                RequestMap map = new RequestMap(NetworkAddress.RECORD_BAR_CLICK);
                                map.put("applyNo", loanStatus4Bar.applyNo);
                                map.put("status", loanStatus4Bar.status);
                                mNetworkAdapter.request(map, MVPUtils.Method.POST, null);
                            }
                        });
                    } else {
                        ll_notice.setVisibility(View.GONE);
                    }
                    if (loanStatus4Bar.coupon!=null&&loanStatus4Bar.coupon.amount > 0) {
                        RedPacketKt.showRedPacket(getActivity(), loanStatus4Bar.coupon);
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                }
            });
        } else {
            ll_notice.setVisibility(View.GONE);
        }
    }

    private void setRepaymentText(int num) {
        amount = num * 100;
        tvBorrowingBalance.setText(num + "");
        tvBorrowingBalance.setSelection(tvBorrowingBalance.length());

    }


    void query_user_status() {//查询用户状态
        mNetworkAdapter.request(NetworkAddress.QUERY_USER_STATUS, MVPUtils.Method.GET, new SimpleViewImpl<UserStatusBean>() {
            @Override
            public void onSuccess(UserStatusBean userStatusBean) {
                dismissLoadingDialog();
                if (userStatusBean.loanStatus4Button != null) {
                    if (userStatusBean.loanStatus4Button.status == BorrowBean.STATE_NOPASS) {
                        if (UCardUtil.isGoneDrainage()) {
                            HfaxDialog hfaxDialog = new HfaxDialog(getActivity());
                            hfaxDialog.setMessage("因综合信用评分不足，暂不符合受理标准");
                            hfaxDialog.setCenterButton("知道了", null);
                            hfaxDialog.show();
                        } else {
                            new Deny(userStatusBean.loanStatus4Button.info, getActivity());
                        }
                    } else if (userStatusBean.loanStatus4Button.toast == 1) {
                        showToast(userStatusBean.loanStatus4Button.info);
                    } else {
                        gotoLoan(userStatusBean);
                    }
                } else {
                    gotoLoan(userStatusBean);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                dismissLoadingDialog();
                showToast(msg);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        isVisible = true;
        startScrollBanner();
    }

    @Override
    public void onStop() {
        super.onStop();
        isVisible = false;
        stopScrollBanner();

    }

    /**
     * 跳转到借款流程页面
     *
     * @param userStatusBean
     */
    void gotoLoan(final UserStatusBean userStatusBean) {
        //保存bean
        UserStatusBean.save(userStatusBean);
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                //身份证认证通过
                if (userStatusBean.getIdCard()) {
                    FaceActivity.start(getContext());
                } else {//身份证认证未通过
                    CertificationActivity.start(getContext(), UCardConstants.IDCARD_LOAN);
                }
            }
        };
        mNetworkAdapter.request(NetworkAddress.QUERY_WINDOW_STATUS, MVPUtils.Method.GET, new SimpleViewImpl<HomeWindowStatusBean>() {
            @Override
            public void onSuccess(HomeWindowStatusBean homeWindowStatusBean) {
                if (homeWindowStatusBean.windowStatus == 1) {
                    new ContractDialog(activity, homeWindowStatusBean.items, new DataChange<Boolean>() {
                        @Override
                        public void onChange(Boolean aBoolean) {
                            if (aBoolean) {
                                run.run();
                            }
                        }
                    });
                } else {
                    run.run();
                }
                dismissLoadingDialog();
            }

            @Override
            public void onFail(int code, String msg) {
                showToast(msg);
                dismissLoadingDialog();
            }
        });
    }

    void submit_repay_info() {//提交还款试算档位信息
        RequestMap map = new RequestMap(NetworkAddress.SUBMIT_REPAY_INFO);
        map.put("loanAmount", amount);
        map.put("periods", period);
        mNetworkAdapter.request(map, MVPUtils.Method.POST, new SimpleViewImpl<QueryTrialRepaymentBean>() {
            @Override
            public void onSuccess(QueryTrialRepaymentBean queryTrialRepaymentBean) {
                query_user_status();
            }

            @Override
            public void onFail(int code, String msg) {
                showToast(msg);
                dismissLoadingDialog();
            }
        });
    }

    void query_trial_repayment(final Callback callback) {//试算月还款列表,成功会回调
        if (amount == 0) {
            if (callback != null) {
                callback.callback(false);
            }
            return;
        }
//        mNetworkAdapter.cancel(NetworkAddress.QUERY_TRIAL_REPAYMENT);
        requestSucces = false;
        RequestMap map = new RequestMap(NetworkAddress.QUERY_TRIAL_REPAYMENT);
        map.put("amount", amount);
        map.put("period", period);
        mNetworkAdapter.request(map, MVPUtils.Method.GET, new SimpleViewImpl<QueryTrialRepaymentBean>() {
            @Override
            public void onSuccess(QueryTrialRepaymentBean queryTrialRepaymentBean) {
                requestSucces = true;
                tv_query_trial_repayment.setText(UCardUtil.formatAmount(queryTrialRepaymentBean.perPeriodInterest));
                if (!UCardUtil.isGoneDrainage()) {
                    ll_root_month.setVisibility(View.VISIBLE);
                }
                if (callback != null) {
                    callback.callback(null);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                showToast(msg);
                if (callback != null) {
                    callback.callback(msg);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        //移除布局变化监听
        getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutChangeListener);
        super.onDestroy();
    }

    @OnFocusChange(value = R.id.tv_borrowing_balance)
    void onFocusChange(View view, boolean focus) {
        switch (view.getId()) {
            case R.id.tv_borrowing_balance:
                if (focus) {
                    line_et.setBackgroundColor(0xffF5894E);
                } else {
                    line_et.setBackgroundColor(0xffE2E2E4);
                }
                break;
        }
    }

    @OnTextChanged(value = R.id.tv_borrowing_balance, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            tvBorrowingBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        } else {
            tvBorrowingBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }
    }

    private int getBorrowMoney(String s) {
        int result = 0;
        if (!TextUtils.isEmpty(s)) {
            try {
                result = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (result > max) {
            result = (int) max;
        } else if (result < min) {
            result = (int) min;
        }
        if (result % 1000 != 0) {
            result = (result + 1000 / 2) / 1000 * 1000;
        }
        return result;
    }

    @Override
    public void onSuccess(HomeBean homeBean) {

    }

    @Override
    public void onFail(int code, String msg) {

    }


    @OnClick({R.id.bt_color_goto_borrow_money, R.id.ll_question, R.id.ll_month, R.id.iv_activity, R.id.iv_strategy, R.id.tv_use})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.ll_question://常见问题
                H5Activity.startActivity(getActivity(), UCardUtil.getH5Url(NetworkAddress.H5_FAQ));
                break;
            case R.id.iv_activity://活动小组
                H5Activity.startActivity(getActivity(), UCardUtil.getH5Url(NetworkAddress.H5_ACTIVITY), "活动中心");
                break;
            case R.id.iv_strategy://借款攻略
                H5Activity.startActivity(getActivity(), UCardUtil.getH5Url(NetworkAddress.H5_STRATEGY), "借款攻略");
                break;
            case R.id.ll_month://时间选择
                PicketDialog picketDialog = new PicketDialog(getActivity(), GlobalConfigUtils.getGlobalConfig().getPeriods(), "", R.layout.dialog_picket_month);
                picketDialog.setDefault(period + "个月");
                picketDialog.setListener(new PicketDialog.OnClickListen() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm(String item) {
                        try {
                            period = Integer.parseInt(item.replace("个月", ""));
                            tv_month.setText(period + "");
                            query_trial_repayment(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                picketDialog.show();
                break;
            case R.id.tv_use://时间选择
                PicketDialog useDialog = new PicketDialog(getActivity(), Arrays.asList("教育", "旅游", "装修", "婚庆", "3C 产品", "家用电器", "其他"), "", R.layout.dialog_picket_month);
                useDialog.setDefault("教育");
                useDialog.setListener(new PicketDialog.OnClickListen() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm(String item) {
                        tv_use.setText(item);
                    }
                });
                useDialog.show();
                break;
            case R.id.bt_color_goto_borrow_money:
                GrowingIOUtils.track(UCardConstants.ANDR_LOAN_BOTTON_CLICK);
                UCardConstants.UCARD_LOAN_BOTTON_CLICK loan_botton_click = new UCardConstants.UCARD_LOAN_BOTTON_CLICK();
                loan_botton_click.loan_Amount = amount;
                loan_botton_click.loan_deadline = period;
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_LOAN_BOTTON_CLICK, loan_botton_click);
                if (!UserModel.isLogin()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    ActivityCallbackUtils.getInstance().putCallback(intent, new Callback() {
                        @Override
                        public void callback(Object o) {
                            if (UserModel.isLogin()) {
                                bt_color_goto_borrow_money.performClick();
                            }
                        }
                    });
                    UCardUtil.startActivity(getActivity(), intent);
                    return;
                }

                String balance = tvBorrowingBalance.getText().toString();
                if (TextUtils.isEmpty(balance)) {
                    showToast("请输入借款金额");
                    return;
                }

                try {
                    int i = Integer.parseInt(balance);
                    if (i * 100 != amount || amount == 0) {
                        int num = getBorrowMoney(balance);
                        setRepaymentText(num);
                        requestSucces = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showLoadingDialog();
                if (!requestSucces) {
                    query_trial_repayment(new Callback() {
                        @Override
                        public void callback(Object o) {
                            if (requestSucces) {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        submit_repay_info();
                                    }
                                }, 300);
                            } else {
                                dismissLoadingDialog();
                            }
                        }
                    });
                } else {
                    submit_repay_info();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GrowingIOUtils.track(UCardConstants.ANDR_HOME_PAGE);
    }

    public void loadBanner() {
        //设置图片加载器
        mBannerList = new ArrayList<>();
        //读取缓存
        BannerModel cache = CacheBean.getCache(BannerModel.class);
        if (cache != null && cache.bannerDetailVos != null && cache.bannerDetailVos.size() > 0) {
            mBannerList.addAll(cache.bannerDetailVos);
            rlBanner.setVisibility(View.VISIBLE);
        } else {
            //添加默认
            mBannerList.add(new BannerBean());
            rlBanner.setVisibility(View.GONE);
        }
        mBannerAdapter = new BannerAdapter(getActivity(), mBannerList);
        hvpViewpager.setAdapter(mBannerAdapter);
        cpiIndicator.setViewPager(hvpViewpager);
        cpiIndicator.setIsLoop(mBannerList.size());
    }


    private void queryBanners() {
        RequestMap map = new RequestMap(NetworkAddress.GET_BANNER);
        mNetworkAdapter.request(map, MVPUtils.Method.GET, new SimpleViewImpl<BannerModel>() {

            @Override
            public void onSuccess(BannerModel banner) {
                banner.saveCache();
                mBannerList.clear();
                if (banner.bannerDetailVos != null && banner.bannerDetailVos.size() > 0) {
                    mBannerList.addAll(banner.bannerDetailVos);
                    rlBanner.setVisibility(View.VISIBLE);
                } else {
                    mBannerList.add(new BannerBean());
                }
                Collections.sort(mBannerList, new Comparator<BannerBean>() {
                    @Override
                    public int compare(BannerBean o1, BannerBean o2) {
                        return o1.showOrder - o2.showOrder;
                    }
                });
                cpiIndicator.setIsLoop(mBannerList.size());
                cpiIndicator.requestLayout();
                mBannerAdapter.notifyDataSetChanged();
                startScrollBanner();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 开始轮播
     */
    public void startScrollBanner() {
        mHandler.removeCallbacks(bannerRunnable);
        mHandler.postDelayed(bannerRunnable, BANNER_INTERVAL);
    }

    /**
     * 停止轮播
     */
    private synchronized void stopScrollBanner() {
        mHandler.removeCallbacks(bannerRunnable);
    }
}
