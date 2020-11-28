package com.hfax.ucard.modules.loan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.lib.BaseApplication;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.InstallAppInfo;
import com.hfax.ucard.bean.LoanInfoBean;
import com.hfax.ucard.bean.LoanSureBean;
import com.hfax.ucard.modules.borrow.adapter.AffirmBankCardAdapter;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.user.AddBankCardActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.FMIdUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.LoanUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.RepetitionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;
import com.hfax.ucard.utils.xinyan.XinyanUtils;
import com.hfax.ucard.widget.FailInfoDialog;
import com.hfax.ucard.widget.HfaxScrollView;
import com.hfax.ucard.widget.RectangleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.hfax.ucard.modules.user.AddBankCardActivity.CARD_TYPE_KEY;

/**
 * 借款确认
 *
 * @author SongGuangyao
 * @date 2018/5/4
 */

public class LoanActivity extends BaseNetworkActivity<LoanSureBean> {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.v_title_divider_)
    View vTitleDivider;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_loan)
    TextView tvLoan;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_loan_time)
    TextView tvLoanTime;
    @BindView(R.id.tv_loan_back)
    TextView tvLoanBack;
    @BindView(R.id.iv_info)
    ImageView ivInfo;
    @BindView(R.id.iv_loan)
    ImageView ivLoan;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.rlv_bg)
    RectangleView rlvBg;
    @BindView(R.id.cb_select_protocol)
    CheckBox cbSelectProtocol;
    @BindView(R.id.tv_protocol)
    TextView tvProtocol;
    @BindView(R.id.cb_bank_list)
    CheckBox cbBankList;
    @BindView(R.id.iv_choose_logo)
    ImageView ivChooseLogo;
    @BindView(R.id.tv_choose_bank)
    TextView tvChooseBank;
    @BindView(R.id.tv_choose_bankInfo)
    TextView tvChooseBankInfo;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.hsl_content)
    HfaxScrollView hslContent;
    @BindView(R.id.iv_add)
    ImageView iv_add;
    LoanInfoBean bean;
    private final static String BANK = "bankcard";

    //是否选择规则
    private boolean choosedRule = true;
    private FailInfoDialog failInfoDialog;
    //是否正在进件请求
    private boolean isLoanRequest = false;
    UCardConstants.UCARD_INSURELOAN_SUBMIT_CLICK insureloan_submit_click = new UCardConstants.UCARD_INSURELOAN_SUBMIT_CLICK();

    @Override
    public void initData() {
        GrowingIOUtils.track(UCardConstants.ANDR_INSURELOAN_PAGE);
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSURELOAN_PAGE, null);

        tvTitle.setText("借款确认");
        vTitleDivider.setVisibility(View.GONE);
        tvTip.setVisibility(View.INVISIBLE);
        ivInfo.setImageResource(R.drawable.icon_steps2_pressed);
        tvInfo.setTextColor(getResources().getColor(R.color.person_info_selected_color));
        ivLoan.setImageResource(R.drawable.icon_steps3_pressed);
        tvLoan.setTextColor(getResources().getColor(R.color.person_info_selected_color));

        //设置头部背景
        View view = findViewById(R.id.ic_tip);
        view.setBackgroundResource(R.drawable.icon_selectpoint3);

        ViewGroup.LayoutParams params = rlvBg.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        rlvBg.setLayoutParams(params);
        loadLoanInfo();
        setProtocol();
        FMIdUtils.init(this);
        cbSelectProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                choosedRule = b;
                checkData();
            }
        });
    }


    /**
     * 加载借款信息
     */
    private void loadLoanInfo() {
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.LOAN_APPLY_INFO, MVPUtils.Method.GET, new SimpleViewImpl<LoanInfoBean>() {
            @Override
            public void onSuccess(LoanInfoBean loanInfoBean) {
                bean=loanInfoBean;
                dismissLoadingDialog();
                tvMoney.setText(UCardUtil.formatAmount(loanInfoBean.loanAmount) + "");
                tvLoanTime.setText(loanInfoBean.periods + "个月");
                tvLoanBack.setText(UCardUtil.getLowAmount(loanInfoBean.repayPerPeriod, loanInfoBean.loanAmount, loanInfoBean.periods));
                hslContent.setVisibility(View.VISIBLE);
                showContentView();
                checkData();
                insureloan_submit_click.loan_Amount = loanInfoBean.loanAmount;
                insureloan_submit_click.loan_deadline = loanInfoBean.periods;
            }

            @Override
            public void onFail(int code, String msg) {
                dismissLoadingDialog();
                showErrorView();
            }
        });
    }

    @Override
    protected void onReLoad() {
        super.onReLoad();
        loadLoanInfo();
    }

    /**
     * 设置默认卡
     */
    private void setDefaultCard(BankCardBean cardBean) {
        if (cardBean != null) {
            tvChooseBank.setText(cardBean.bankName);
            tvChooseBankInfo.setText("尾号" + UCardUtil.getBankNumLast(cardBean.cardNo));
            GlideUtils.requestImageCode(this, UCardUtil.getBankCardLogo(cardBean.bankCode, false), ivChooseLogo);
        }
    }

    /**
     * 设置协议
     */
    private void setProtocol() {
        String protocol_1 = " 合同信息";
        String protocolText = "阅读并同意" + protocol_1;
        SpannableString spanString = new SpannableString(protocolText);
        int startIndex = protocolText.indexOf(protocol_1);
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.authorize_url_color));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                H5Activity.startActivity(getThis(), String.format(UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT), "", "application"));
            }
        }, startIndex, startIndex + protocol_1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProtocol.setHighlightColor(Color.TRANSPARENT);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        tvProtocol.setText(spanString);
    }

    @Override
    public void onSuccess(LoanSureBean bean) {
        dismissLoadingDialog();
        isLoanRequest = false;
        MainActivity.start(this);
        StatusActivity.start(BaseApplication.getContext(), UCardConstants.STATUS_LOAN, bean.applyNo);
        finish();
        insureloan_submit_click.submit_loan_result = UCardConstants.UCARD_SDA_SUCCEED;
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSURELOAN_SUBMIT_CLICK, insureloan_submit_click);
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        isLoanRequest = false;
        switch (code) {
            case NetworkAddress.CODE_ERROR_MSG_FACR:
            case NetworkAddress.CODE_ERROR_MSG_ID:
            case NetworkAddress.CODE_ERROR_MSG_AUTH:
            case NetworkAddress.CODE_ERROR_MSG_PERSON:
                if (failInfoDialog == null) {
                    failInfoDialog = new FailInfoDialog(this, msg, null, "返回首页");
                }
                failInfoDialog.setListener(new FailInfoDialog.OnClickListen() {
                    @Override
                    public void onClick() {
                        MainActivity.start(getThis());
                    }
                });
                failInfoDialog.show();
                failInfoDialog.setCancelable(false);
                failInfoDialog.setCanceledOnTouchOutside(false);
            case NetworkAddress.CODE_ERROR_MSG_FAILE://失败
                if (failInfoDialog == null) {
                    failInfoDialog = new FailInfoDialog(this, getResources().getString(R.string.loan_failed_title), getResources().getString(R.string.loan_failed_msg));
                }
                failInfoDialog.setListener(new FailInfoDialog.OnClickListen() {
                    @Override
                    public void onClick() {
                        MainActivity.start(getThis());
                    }
                });
                failInfoDialog.show();
                failInfoDialog.setCancelable(false);
                failInfoDialog.setCanceledOnTouchOutside(false);
                break;
            default:
                showToast(msg);
                break;
        }
        insureloan_submit_click.submit_loan_result = UCardConstants.UCARD_SDA_FAILED;
        insureloan_submit_click.error_type = msg;
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_INSURELOAN_SUBMIT_CLICK, insureloan_submit_click);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_loan;
    }


    @OnClick({R.id.iv_title_return, R.id.tv_next, R.id.add_bank})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_INSURELOAN_SUBMIT_CLICK);
                submitLoan();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isLoanRequest && KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void start(Context context, BankCardBean bankCardBean) {
        Intent intent = new Intent(context, LoanActivity.class);
        intent.putExtra(BANK, bankCardBean);
        UCardUtil.startActivity(context, intent);
    }


    /**
     * 提交借款
     */
    public void submitLoan() {
        if (!choosedRule) {
            showToast("请阅读并同意协议");
            return;
        }
        showLoadingDialog();
        isLoanRequest = true;
        LoanUtils.getLoanInfo(this, new LoanUtils.CallBack() {
            @Override
            public void callBack(final Map<String, Object> infoMap) {
                final RequestMap map = new RequestMap(NetworkAddress.SUBMIT_LOAN_APPLY);
                try {
                    if (getIntent().hasExtra(BANK)) {
                        BankCardBean bankCardBean = (BankCardBean) getIntent().getSerializableExtra(BANK);
                        if (bankCardBean != null) {
                            map.put("bankCode", bankCardBean.bankCode);
                            map.put("cardNo", bankCardBean.cardNo);
                        }
                    }
                } catch (Exception e) {
                }
                map.putAll(infoMap);
                if(bean!=null){
                    map.put("preApplyNo",bean.preApplyNo);
                }
                if (((List<InstallAppInfo>) infoMap.get("appList")).size() > 0) {
                    insureloan_submit_click.applist = UCardConstants.UCARD_SDA_YES;
                } else {
                    insureloan_submit_click.applist = UCardConstants.UCARD_SDA_NO;
                }
                XinyanUtils.getXinyan(getThis(), new XinyanUtils.CallBack() {
                    @Override
                    public void callBack(String token, String sign) {
                        map.put("xyBmToken", token);
                        map.put("xyBmSign", sign);
                        submit(map);
                    }

                    @Override
                    public void error(String msg) {
                        submit(map);
                    }
                });
            }

            @Override
            public void error(String msg) {
                showToast("借款异常，请重试");
                dismissLoadingDialog();
            }
        });
    }

    private void submit(final RequestMap map) {
        RepetitionUtils.getRepetition().submit(map, LoanActivity.this, new DataChange<Boolean>() {
            @Override
            public void onChange(Boolean aBoolean) {
                if (aBoolean) {
                    mNetworkAdapter.request(map, MVPUtils.Method.POST);
                } else {
                    dismissLoadingDialog();
                }
            }
        });
    }

    /**
     * 检查数据
     *
     * @return
     */
    private void checkData() {
        boolean isComplete = true;
        if (!choosedRule) {
            isComplete = false;
        }

        tvNext.setEnabled(isComplete);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XinyanUtils.destory();
    }
}
