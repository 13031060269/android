package com.hfax.ucard.modules.borrow.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.lib.network.BaseResponse;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.MyFragmentActivity;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.BorrowDetails;
import com.hfax.ucard.bean.QueryTrialRepaymentBean;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.borrow.MakeMoneyFragment;
import com.hfax.ucard.modules.borrow.adapter.AffirmBankCardAdapter;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.user.AddBankCardActivity;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.RepetitionUtils;
import com.hfax.ucard.utils.SpannableStringUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.widget.CardChangeDialog;
import com.hfax.ucard.widget.FailInfoDialog;
import com.hfax.ucard.widget.PlanView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by liuweiping on 2018/5/3.
 */

public class AffirmNeedBorrowFragment extends BorrowDetailsFragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cb_contract)
    CheckBox cb_contract;
    @BindView(R.id.cb_repayment_plan)
    CheckBox cb_repayment_plan;
    //    @BindView(R.id.cb_bank_list)
//    CheckBox cb_bank_list;
    @BindView(R.id.cb_bank_list_2)
    CheckBox cb_bank_list2;
    //    @BindView(R.id.lv_borrow)
//    ListView lv_borrow;
    @BindView(R.id.lv_repay)
    ListView lv_repay;
    @BindView(R.id.plan_view)
    PlanView plan_view;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_repayment)
    TextView tv_repayment;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_deadline)
    TextView tv_deadline;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.iv_logo_credit)
    ImageView iv_logo_credit;
    @BindView(R.id.tv_bank_name_credit)
    TextView tv_bank_name_credit;
    @BindView(R.id.tv_bank_num_credit)
    TextView tv_bank_num_credit;
    @BindView(R.id.iv_logo_debit)
    ImageView iv_logo_debit;
    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.tv_bank_name_debit)
    TextView tv_bank_name_debit;
    @BindView(R.id.tv_bank_num_debit)
    TextView tv_bank_num_debit;
    @BindView(R.id.tv_change_bank)
    TextView tv_change_bank;
    @BindView(R.id.add_bank)
    View add_bank;
    @BindView(R.id.bank_info)
    View bank_info;
    @BindView(R.id.tv_add_name)
    TextView tv_add_name;
    @BindView(R.id.tv_add_bank_haved)
    TextView tv_add_bank_haved;
    @BindView(R.id.ll_add_root)
    View ll_add_root;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.ll_period)
    View ll_period;
    @BindView(R.id.tv_msg)
    TextView tv_msg;
    AffirmBankCardAdapter adapterRepay = new AffirmBankCardAdapter(AffirmBankCardAdapter.Type.repay);
    CountDownTimer countDownTimer;
    FailInfoDialog failInfoDialog;
    private boolean isFinish;//确认要款倒计时结束
    String applyNo;
    BankCardBean repayCardVo;
    int orderType;
    int confirmLoanPeriod;
    BorrowDetails mBorrowDetails;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_need_borrow_money;
    }

    void query_trial_repayment(final int period) {//试算月还款列表
        showLoadingDialog();
        RequestMap map = new RequestMap(NetworkAddress.QUERY_TRIAL_REPAYMENT);
        map.put("amount", mBorrowDetails.loanAmount);
        map.put("period", period);
        confirmLoanPeriod = period;
        checkSubmit();
        mNetworkAdapter.request(map, MVPUtils.Method.GET, new SimpleViewImpl<QueryTrialRepaymentBean>() {
            @Override
            public void onSuccess(QueryTrialRepaymentBean queryTrialRepaymentBean) {
                tv_repayment.setText(UCardUtil.formatAmount(queryTrialRepaymentBean.perPeriodInterest));
                tv_deadline.setText(period + "个月");
                dismissLoadingDialog();
            }

            @Override
            public void onFail(int code, String msg) {
                tv_repayment.setText("---");
                tv_deadline.setText(period + "个月");
                showToast(msg);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public void initData() {
        mBorrowDetails = (BorrowDetails) getArguments().getSerializable(BorrowDetailsActivity.KEY_DATA);
        if (mBorrowDetails == null) {
            showErrorView();
            return;
        }
        applyNo = mBorrowDetails.applyNo;
        orderType = mBorrowDetails.orderType;
        tvTitle.setText("确认要款");
        tv_add_name.setText("添加收/还款卡");
        tv_add_bank_haved.setText("添加收/还款卡");
        cb_contract.setChecked(true);
        SpannableStringUtils.INSTANCE.setColor("阅读并同意《借款合同》，并声明本人从惠金小贷（广州惠金小额贷款" +
                "有限公司）借款，借款利率是年化24%以内。本人确认：惠金小贷未" +
                "收取借款本息以外的任何其他款项或费用。", "《借款合同》", 0xff363349, tv_msg, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                H5Activity.startActivity(getActivity(), String.format(UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT), applyNo + "&orderType=" + orderType, "confirm"));
                return null;
            }
        });

        if (mBorrowDetails.approvePeriods != null && mBorrowDetails.approvePeriods.length > 0) {
            ll_period.setVisibility(View.VISIBLE);
            rg.removeAllViews();
            int[] ints = Arrays.copyOf(mBorrowDetails.approvePeriods, 4);
            String suffix = "个月";
            for (final int period : ints) {
                RadioButton inflate = (RadioButton) getLayoutInflater().inflate(R.layout.item_affirm_rb, rg, false);
                inflate.setId(UCardUtil.generateViewId());
                inflate.setChecked(false);
                if (period != 0) {
                    inflate.setText(String.format("%s%s", period, suffix));
                    inflate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                query_trial_repayment(period);
                            }
                        }
                    });
                } else {
                    inflate.setVisibility(View.INVISIBLE);
                }
                rg.addView(inflate);
            }

        } else {
            ll_period.setVisibility(View.GONE);
            tv_repayment.setText(UCardUtil.getLowAmount(mBorrowDetails.repayMonth, mBorrowDetails.loanAmount, mBorrowDetails.periods));
            tv_deadline.setText(mBorrowDetails.periods + "个月");
            confirmLoanPeriod = mBorrowDetails.periods;
        }

        lv_repay.setAdapter(adapterRepay);
        adapterRepay.setChangeListener(new DataChange<BankCardBean>() {
            @Override
            public void onChange(BankCardBean bankCardBean) {
                setDebit2(bankCardBean);
            }
        });

        setDebitBank(mBorrowDetails);
//        cb_bank_list.setChecked(false);
        cb_bank_list2.setChecked(false);
        if (!UCardUtil.isCollectionEmpty(mBorrowDetails.orderInfoVos)) {
            plan_view.setPlan(mBorrowDetails.orderInfoVos);
        }

        tv_money.setText(UCardUtil.formatAmount(mBorrowDetails.loanAmount));
        tv_date.setText("申请日期 " + mBorrowDetails.applyDate);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (mBorrowDetails.confirmLeftTime > 0) {
            isFinish = false;
            countDownTimer = new CountDownTimer(mBorrowDetails.confirmLeftTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tv_time.setText("（" + UCardUtil.getTime(millisUntilFinished) + "）");
                }

                @Override
                public void onFinish() {
                    isFinish = true;
                    submit.setEnabled(false);
                }
            };
            countDownTimer.start();
        } else {
            isFinish = true;
        }
        tv_change_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardChangeDialog.checkBank(AffirmNeedBorrowFragment.this, mBorrowDetails, new ActivityCallbackUtils.Callback() {
                    @Override
                    public void callback(Object o) {
                        if ((o instanceof Boolean) && (Boolean) o) {
                            setNeedRefresh(true);
                        }
                    }
                });
            }
        });
        checkSubmit();
    }

    private void setDebitBank(BorrowDetails mBorrowDetails) {
        repayCardVo = null;
        switch (mBorrowDetails.orderType) {
            default:
            case 1:
                mBorrowDetails.initRepayCardVo();
                setDebit(mBorrowDetails.repayCardVo);
                break;
            case 2:
                adapterRepay.setData(mBorrowDetails.repayCardVos);
                if (!UCardUtil.isCollectionEmpty(mBorrowDetails.repayCardVos) && mBorrowDetails.repayCardVos.size() > 1) {
                    cb_bank_list2.setVisibility(View.VISIBLE);
                } else {
                    cb_bank_list2.setVisibility(View.GONE);
                }
                if (!UCardUtil.isCollectionEmpty(mBorrowDetails.repayCardVos)) {
                    ll_add_root.setVisibility(View.GONE);
                    tv_add_bank_haved.setVisibility(View.VISIBLE);
                } else {
                    ll_add_root.setVisibility(View.VISIBLE);
                    tv_add_bank_haved.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 还款银行卡
     *
     * @param bankCardBean
     */
    private void setDebit(BankCardBean bankCardBean) {
        repayCardVo = bankCardBean;
        cb_bank_list2.setVisibility(View.GONE);
        cb_bank_list2.setChecked(false);
        if (bankCardBean != null) {
            add_bank.setVisibility(View.GONE);
            bank_info.setVisibility(View.VISIBLE);
            tv_bank_name_debit.setText(bankCardBean.bankName);
            tv_bank_num_debit.setText("尾号" + UCardUtil.getBankNumLast(bankCardBean.cardNo));
            GlideUtils.requestImageCode(getActivity(), UCardUtil.getBankCardLogo(bankCardBean.bankCode, false), iv_logo_debit);
            tv_change_bank.setVisibility(View.VISIBLE);
        } else {
            add_bank.setVisibility(View.VISIBLE);
            bank_info.setVisibility(View.GONE);
        }
    }

    /**
     * 还款银行卡 纯小贷
     *
     * @param bankCardBean
     */
    private void setDebit2(BankCardBean bankCardBean) {
        repayCardVo = bankCardBean;
        if (bankCardBean != null) {
            bank_info.setVisibility(View.VISIBLE);
            tv_bank_name_debit.setText(bankCardBean.bankName);
            tv_bank_num_debit.setText("尾号" + UCardUtil.getBankNumLast(bankCardBean.cardNo));
            GlideUtils.requestImageCode(getActivity(), UCardUtil.getBankCardLogo(bankCardBean.bankCode, false), iv_logo_debit);
        } else {
            bank_info.setVisibility(View.GONE);
        }
        tv_change_bank.setVisibility(View.GONE);
        add_bank.setVisibility(View.VISIBLE);
        cb_bank_list2.setChecked(false);
    }


    @OnCheckedChanged({R.id.cb_contract, R.id.cb_bank_list_2})
    void onChenck(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()) {
//            case R.id.cb_bank_list:
//                if (checked) {
//                    lv_borrow.setVisibility(View.VISIBLE);
//                    UCardUtil.setListViewHeightByItem(lv_borrow);
//                } else {
//                    lv_borrow.setVisibility(View.GONE);
//                }
//                break;
            case R.id.cb_bank_list_2:
                if (checked) {
                    lv_repay.setVisibility(View.VISIBLE);
                    UCardUtil.setListViewHeightByItem(lv_repay);
                } else {
                    lv_repay.setVisibility(View.GONE);
                }
                break;
            case R.id.cb_contract:
                checkSubmit();
                break;
        }
    }

    private void checkSubmit() {
        if (!isFinish && cb_contract.isChecked() && repayCardVo != null && confirmLoanPeriod != 0) {
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
        }
    }

    @OnClick({R.id.iv_title_return, R.id.tv_change_bank, R.id.submit, R.id.add_bank,R.id.tv_msg})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                getActivity().finish();
                break;
            case R.id.submit:
                if (repayCardVo == null) return;
                showLoadingDialog();
                //确认要款
                final RequestMap requestMap = new RequestMap(NetworkAddress.CONFIRM_LOAN);
                requestMap.put("agree", true);
                requestMap.put("applyNo", applyNo);
                requestMap.put("mobile", repayCardVo.reserveMobile);
                requestMap.put("repayBankCode", repayCardVo.bankCode);
                requestMap.put("repayCardNum", repayCardVo.cardNo);
                if (confirmLoanPeriod != 0) {
                    requestMap.put("confirmLoanPeriod", confirmLoanPeriod);
                }
                RepetitionUtils.getRepetition().submit(requestMap, this, new DataChange<Boolean>() {
                    @Override
                    public void onChange(Boolean aBoolean) {
                        if (aBoolean) {
                            mNetworkAdapter.request(requestMap, MVPUtils.Method.POST);
                        } else {
                            dismissLoadingDialog();
                        }
                    }
                });
                break;
            case R.id.add_bank:
                String type = BankCardBean.TYPE_DEBIT;
                if (orderType == 2) {
                    type = BankCardBean.TYPE_PLATFORM;
                }
                AddBankCardActivity.start(getActivity(), type, new ActivityCallbackUtils.Callback() {
                    @Override
                    public void callback(Object o) {
                        if ((o instanceof Boolean) && (Boolean) o) {
                            setNeedRefresh(true);
                        }
                    }
                });
            case R.id.tv_msg:
                cb_contract.setChecked(!cb_contract.isChecked());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void borrowChange(BorrowDetails borrowDetails) {
        setNeedRefresh(false);
        if (borrowDetails == null) return;
        setDebitBank(borrowDetails);
        checkSubmit();
    }

    @Override
    public void onSuccess(Object o) {
        dismissLoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putString("applyNo", applyNo);
        MyFragmentActivity.start(getActivity(), MakeMoneyFragment.class, bundle);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onFail(final int code, String msg) {
        dismissLoadingDialog();
        if (code == 804002) {//重新验卡
            FailInfoDialog dialog = new FailInfoDialog(getActivity(), "银行卡信息有误，请修改！", "可能是银行卡号与银行预留手机号不匹配", "去修改");
            dialog.setListener(new FailInfoDialog.OnClickListen() {
                @Override
                public void onClick() {
                    String type = BankCardBean.TYPE_DEBIT;
                    if (orderType == 2) {
                        type = BankCardBean.TYPE_PLATFORM;
                    }
                    AddBankCardActivity.start(getActivity(), type, applyNo, repayCardVo, new ActivityCallbackUtils.Callback() {
                        @Override
                        public void callback(Object o) {
                            if ((o instanceof Boolean) && (Boolean) o) {
                                setNeedRefresh(true);
                            }
                        }
                    });
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            return;
        }

        if (code == BaseResponse.NETWORK_ERROR) {
            showToast(msg);
            return;
        }
        if (failInfoDialog == null) {
            failInfoDialog = new FailInfoDialog(getActivity(), "确认要款失败", msg);
            failInfoDialog.setListener(new FailInfoDialog.OnClickListen() {
                @Override
                public void onClick() {
                    if (code != NetworkAddress.AFFIRM_BANKCARD_DELETE) {
                        MainActivity.start(getActivity());
                    }
                }
            });
        }
        failInfoDialog.show();
        failInfoDialog.setCanceledOnTouchOutside(false);
        failInfoDialog.setCancelable(false);
    }
}
