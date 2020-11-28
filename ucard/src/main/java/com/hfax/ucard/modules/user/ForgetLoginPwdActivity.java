package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.ForgetLoginPwdBean;
import com.hfax.ucard.bean.SmsCodeBean;
import com.hfax.ucard.utils.Constants.SmsCodeSceneConstant;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PWDUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.YunPianUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.widget.codes.CodeView;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 忘记密码
 *
 * @author SongGuangyao
 * @date 2018/4/23
 */

public class ForgetLoginPwdActivity extends BaseNetworkActivity<ForgetLoginPwdBean> implements TextWatcher {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.cv_code)
    CodeView cvCode;
    @BindView(R.id.tv_next)
    TextView tvNext;
    private static final String PHONE = "phone";

    /**
     * 手机号码
     */
    private String phone;
//    private SmsCodeBean smsCodeBean;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_forget_login_pwd;
    }

    @Override
    public void initData() {
        tvTitle.setText("忘记密码");
        phone = getIntent().getStringExtra(PHONE);
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
        }
//        cvCode.loadLast(phone, SmsCodeSceneConstant.RESET_PASSWORD);
        cvCode.setListener(new CodeView.CusOnClickListener() {
            @Override
            public void onSuccess(SmsCodeBean mBean) {
//                if (mBean != null) {
//                    smsCodeBean = mBean;
//                }
                Utils.hideInputMethod(ForgetLoginPwdActivity.this);
            }

            @Override
            public void onFail(int code, String msg) {
                Utils.hideInputMethod(ForgetLoginPwdActivity.this);
            }
        });

        etPhone.addTextChangedListener(this);
        etCode.addTextChangedListener(this);
        etPwd.addTextChangedListener(this);
    }

    public static void start(Context context, String phone) {
        Intent intent = new Intent(context, ForgetLoginPwdActivity.class);
        intent.putExtra(PHONE, phone);
        UCardUtil.startActivity(context, intent);
    }


    @OnClick({R.id.iv_title_return, R.id.cv_code, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                Utils.hideInputMethod(this);
                finish();
                break;
            case R.id.cv_code:
                phone = etPhone.getText().toString();
                if (!Utils.isMobileNO(phone)) {
                    showToast(getString(R.string.toast_phone_error));
                    return;
                }
                cvCode.requestSmsCode(phone, SmsCodeSceneConstant.RESET_PASSWORD);
                break;
            case R.id.tv_next:
                nextToSet();
                break;
        }
    }

    private void nextToSet() {
        phone = etPhone.getText().toString();
        final String msgCode = etCode.getText().toString();
        final String password = etPwd.getText().toString();

        if (!Utils.isMobileNO(phone)) {
            showToast(getString(R.string.toast_phone_error));
            return;
        }

        if (!PWDUtils.isRightPwd(password)) {
            showToast("密码为8-16位字符，需同时包含字母、数字");
            return;
        }
        Utils.hideInputMethod(this);
        YunPianUtils.getYunPian().requestYunPian(this, new DataChange<Map<String, String>>() {
            @Override
            public void onChange(Map<String, String> stringStringMap) {
                if (stringStringMap != null) {
                    showLoadingDialog();
                    RequestMap map = new RequestMap(NetworkAddress.RESET_PASSWORD);
                    map.put("mobile", phone);
                    map.put("password", UserModel.encodePwd(password));
                    map.put("sms-text", msgCode);
                    map.putAll(stringStringMap);
                    mNetworkAdapter.request(map, MVPUtils.Method.POST);
                }
            }
        });
    }

    @Override
    public void onSuccess(ForgetLoginPwdBean forgetLoginPwdBean) {
        dismissLoadingDialog();
        showToast("设置成功");
//        ActivityCallbackUtils.getInstance().execute(getIntent(), true);
        finish();
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }


    /**
     * 检查数据
     */
    public void checkData() {
        boolean isComplete = true;
        String msgCode = etCode.getText().toString();
        String password = etPwd.getText().toString();
        String phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            isComplete = false;
        }
        if (TextUtils.isEmpty(msgCode)) {
            isComplete = false;
        }
        if (TextUtils.isEmpty(password)) {
            isComplete = false;
        }
        tvNext.setEnabled(isComplete);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkData();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
