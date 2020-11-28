package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.h5.H5DepositActivity;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.GsonUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.BankCardBean;
import com.hfax.ucard.bean.GlobalConfigBean;
import com.hfax.ucard.bean.SmsCodeBean;
import com.hfax.ucard.utils.Constants.SmsCodeSceneConstant;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GlobalConfigUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.glide.GlideUtils;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.widget.BankCardEditText;
import com.hfax.ucard.widget.codes.BankCodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class AddBankCardActivity extends BaseNetworkActivity<Object> {
    public static final String CARD_TYPE_KEY = "from";
    public static final String APPLY_NO = "applyNo";
    public static final String BANKCARD = "bankcard";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title2)
    TextView tvTitle2;
    @BindView(R.id.tv_submit)
    TextView tv_submit;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.iv_bank_logo)
    ImageView iv_bank_logo;
    @BindView(R.id.tv_bank_name_nobank)
    TextView tv_bank_name_nobank;
    @BindView(R.id.tv_card_type_text)
    TextView tv_card_type_text;
    @BindView(R.id.code_view2)
    BankCodeView code_view2;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_card_num)
    BankCardEditText et_card_num;
    String applyNo;

    @BindView(R.id.et_sms)
    EditText et_sms;

    private String type = BankCardBean.TYPE_DEBIT;
    String codeType = SmsCodeSceneConstant.BIND_DEBIT;
    private GlobalConfigBean.Unit unit;
    SmsCodeBean mSmsCodeBean;
    private UCardConstants.UCARD_ADD_CARD cardInfo;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public void initData() {
        if (getIntent().hasExtra(CARD_TYPE_KEY)) {
            type = getIntent().getStringExtra(CARD_TYPE_KEY);
        }
        if (getIntent().hasExtra(APPLY_NO)) {
            applyNo = getIntent().getStringExtra(APPLY_NO);
        }
        if (getIntent().hasExtra(BANKCARD)) {
            try {
                BankCardBean bankCardBean = (BankCardBean) getIntent().getSerializableExtra(BANKCARD);
                if (bankCardBean != null) {
                    List<GlobalConfigBean.Unit> banks;
                    switch (type) {
                        case BankCardBean.TYPE_CREDIT:
                            banks = GlobalConfigUtils.getGlobalConfig().creditBank;
                            break;
                        default:
                        case BankCardBean.TYPE_DEBIT:
                            banks = GlobalConfigUtils.getGlobalConfig().debitBank;
                            break;
                    }
                    for (GlobalConfigBean.Unit unit : banks) {
                        if (TextUtils.equals(unit.key, bankCardBean.bankCode)) {
                            this.unit = unit;
                            break;
                        }
                    }
                    if (unit != null) {
                        tv_bank_name.setText(unit.value);
                        GlideUtils.requestImageCode(this, UCardUtil.getBankCardLogo(unit.key, false), iv_bank_logo);
                        tv_bank_name_nobank.setVisibility(View.GONE);
                    }
                    et_card_num.setText(bankCardBean.cardNo);
                    et_phone.setText(bankCardBean.reserveMobile);
                }
            } catch (Exception e) {
            }
        }
        cardInfo = new UCardConstants.UCARD_ADD_CARD();
        String title;
        String title2;
        switch (type) {
            default:
            case BankCardBean.TYPE_CREDIT:
                title = "添加信用卡";
                title2 = "请输入本人名下信用卡信息";
                tv_card_type_text.setText("信用卡卡号");
                et_card_num.setHint("请输入信用卡卡号");
                codeType = SmsCodeSceneConstant.BIND_CREDIT;
                cardInfo.type = UCardConstants.UCARD_SDA_CREDIT;
                break;
            case BankCardBean.TYPE_DEBIT:
                title = "添加收/还款银行卡";
                title2 = "请输入收/还款银行卡信息";
                cardInfo.type = UCardConstants.UCARD_SDA_BANK;
                break;
            case BankCardBean.TYPE_CHANGE_DEBIT:
                title = "变更收/还款银行卡";
                title2 = "请输入收/还款银行卡信息";
                cardInfo.type = UCardConstants.UCARD_SDA_BANK_CHANGE;
                break;
            case BankCardBean.TYPE_PLATFORM:
                title = "添加收/还款银行卡";
                title2 = "请输入收/还款银行卡信息";
                codeType = SmsCodeSceneConstant.BIND_PLATFORM;
                cardInfo.type = UCardConstants.UCARD_SDA_PLATFORM;
                break;
            case BankCardBean.TYPE_CHANGE_PLATFORM:
                title = "变更收/还款银行卡";
                title2 = "请输入收/还款银行卡信息";
                codeType = SmsCodeSceneConstant.BIND_PLATFORM;
                cardInfo.type = UCardConstants.UCARD_SDA_PLATFORM_CHANGE;
                break;
        }
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_PAGE, cardInfo);
        tvTitle.setText(title);
        tvTitle2.setText(title2);
        code_view2.setListener(new BankCodeView.CusOnClickListener() {
            @Override
            public void onSuccess(SmsCodeBean bean) {
                mSmsCodeBean = bean;
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_BUTTON_SMS_CLICK, cardInfo);
            }

            @Override
            public void onFail(int code, String msg) {
                cardInfo.result = UCardConstants.UCARD_SDA_FAILED;
                cardInfo.error_type = msg;
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_BUTTON_SMS_CLICK, cardInfo);
            }
        });
    }

    public static void start(Context context, String type, ActivityCallbackUtils.Callback... callbacks) {
        start(context, type, null, null, callbacks);
    }

    public static void start(Context context, String type, String applyNo, ActivityCallbackUtils.Callback... callbacks) {
        start(context, type, applyNo, null, callbacks);
    }

    public static void start(Context context, String type, String applyNo, BankCardBean bankCardBean, ActivityCallbackUtils.Callback... callbacks) {
        Intent intent = new Intent(context, AddBankCardActivity.class);
        intent.putExtra(CARD_TYPE_KEY, type);
        intent.putExtra(APPLY_NO, applyNo);
        intent.putExtra(BANKCARD, bankCardBean);
        ActivityCallbackUtils.getInstance().putCallback(intent, callbacks);
        UCardUtil.startActivity(context, intent);
    }

    @OnClick({R.id.iv_title_return, R.id.tv_submit, R.id.code_view2, R.id.select_bank})
    public void onViewClicked(View view) {
        String mobile;
        String cardNo;
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.select_bank:
                String from = "";
                switch (type) {
                    case BankCardBean.TYPE_CREDIT:
                        from = BankCardBean.TYPE_CREDIT;
                        break;
                    default:
                        break;
                }
                BankCardSelectActivity.start(this, from);

                break;
            case R.id.tv_submit://
                if (mSmsCodeBean == null) {
                    showToast("请先发送验证码");
                    return;
                }
                mobile = et_phone.getText().toString().trim();
                cardNo = et_card_num.getCardNum();
                String smsText = et_sms.getText().toString().trim();
                RequestMap map = new RequestMap(NetworkAddress.ADD_CREDIT_CARD);
                map.put("bankCode", unit.key);
                map.put("cardNo", cardNo);
                map.put("mobile", mobile);
                map.put("sms-text", smsText);
                map.put("flowId", mSmsCodeBean.flowId);
                switch (type) {
                    default:
                    case BankCardBean.TYPE_CREDIT:
                        GrowingIOUtils.track(UCardConstants.ANDR_ADDCREDIT_FINISH_CLICK);
                        mNetworkAdapter.request(map, MVPUtils.Method.POST);
                        showLoadingDialog();
                        break;
                    case BankCardBean.TYPE_DEBIT:
                        map.clear();
                        map.put("bank-code", unit.key);
                        map.put("card-no", cardNo);
                        map.put("mobile", mobile);
                        map.put("sms-text", smsText);
                        map.put("flow-id", mSmsCodeBean.flowId);
                        map.setPath(NetworkAddress.ADD_DEBIT_CARD);
                        H5DepositActivity.startDepositActivityForResult(this, Utils.getApiURL(map.getPath()), "", Utils.getRequestParams(map), 1);
                        break;
                    case BankCardBean.TYPE_CHANGE_DEBIT:
                        map.clear();
                        map.put("bank-code", unit.key);
                        map.put("card-no", cardNo);
                        map.put("mobile", mobile);
                        map.put("sms-text", smsText);
                        map.put("flow-id", mSmsCodeBean.flowId);
                        map.setPath(NetworkAddress.CHANGE_DEBIT_CARD);
                        H5DepositActivity.startDepositActivityForResult(this, Utils.getApiURL(map.getPath()), "", Utils.getRequestParams(map), 1);
                        break;
                    case BankCardBean.TYPE_PLATFORM:
                        showLoadingDialog();
                        map.put("operType", "ADD");
                        map.setPath(NetworkAddress.ADD_OR_CHANGE_PLATFORM_CARD);
                        mNetworkAdapter.request(map, MVPUtils.Method.POST);
                        break;
                    case BankCardBean.TYPE_CHANGE_PLATFORM:
                        showLoadingDialog();
                        map.put("operType", "CHANGE");
                        map.put("applyNo", applyNo);
                        map.setPath(NetworkAddress.ADD_OR_CHANGE_PLATFORM_CARD);
                        mNetworkAdapter.request(map, MVPUtils.Method.POST);
                        break;
                }
                break;
            case R.id.code_view2://验证码
                mobile = et_phone.getText().toString().trim();
                if (!Utils.isMobile(mobile)) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if (unit == null) {
                    showToast("请选择银行");
                    return;
                }
                cardNo = et_card_num.getCardNum();
                if (cardNo.length() < 1) {
                    showToast("请输入正确的银行卡号");
                    return;
                }
                code_view2.requestSmsCode(unit.key, et_card_num.getCardNum(), et_phone.getText().toString(), codeType);
                break;
        }
    }

    @OnTextChanged(value = {R.id.et_phone, R.id.et_card_num, R.id.et_sms}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {
        dataChange();
    }

    void dataChange() {
        boolean result = true;
        if (unit == null) {
            result = false;
        }
        String mobile = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            result = false;
        }
        String cardNo = et_card_num.getCardNum();
        if (cardNo.length() < 1) {
            result = false;
        }
        String smsText = et_sms.getText().toString().trim();
        if (TextUtils.isEmpty(smsText)) {
            result = false;
        }
        tv_submit.setEnabled(result);
    }

    @Override
    public void onSuccess(Object obj) {
        dismissLoadingDialog();
        ActivityCallbackUtils.getInstance().execute(getIntent(), true);
        finish();
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_FINISH_CLICK, cardInfo);
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
        cardInfo.result = UCardConstants.UCARD_SDA_FAILED;
        cardInfo.error_type = msg;
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_FINISH_CLICK, cardInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (UCardConstants.COMMON_RESULT_CODE == resultCode) {
            String stringExtra = data.getStringExtra(UCardConstants.COMMON_RESULT_DATA);
            if (!TextUtils.isEmpty(stringExtra)) {
                if (1 == requestCode) {
                    try {
                        JSONObject jsonObject = new JSONObject(stringExtra);
                        String flag = jsonObject.optString(UCardConstants.FLAG);
                        String msg = jsonObject.optString(UCardConstants.MSG);
                        if (TextUtils.equals("1", flag) && !TextUtils.isEmpty(msg)) {
                            showToast(msg);
                            cardInfo.result = UCardConstants.UCARD_SDA_FAILED;
                            cardInfo.error_type = msg;
                            GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_FINISH_CLICK, cardInfo);
                        } else if (TextUtils.equals("0", flag) || TextUtils.equals("2", flag) || TextUtils.equals("3", flag)) {
                            ActivityCallbackUtils.getInstance().execute(getIntent(), true);
                            GrowingIOUtils.trackSDA(UCardConstants.UCARD_ADD_CARD_FINISH_CLICK, cardInfo);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (requestCode == BankCardSelectActivity.KEY_REQUEST_CODE) {
                    unit = GsonUtils.json2Bean(stringExtra, GlobalConfigBean.Unit.class);
                    tv_bank_name.setText(unit.value);
                    GlideUtils.requestImageCode(this, UCardUtil.getBankCardLogo(unit.key, false), iv_bank_logo);
                    tv_bank_name_nobank.setVisibility(View.GONE);
                    dataChange();
                }
            }
        }
    }
}
