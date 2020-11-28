package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.hfax.app.h5.H5Activity;
import com.hfax.app.utils.EventBusUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.bean.SmsCodeBean;
import com.hfax.ucard.utils.Constants.SmsCodeSceneConstant;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.FMIdUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.LocationUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.MacUtils;
import com.hfax.ucard.utils.PWDUtils;
import com.hfax.ucard.utils.PermissionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.widget.codes.CodeView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

/**
 * 注册页面
 *
 * @author SongGuangyao
 * @date 2018/4/23
 */

public class RegisterActivity extends BaseNetworkActivity<LoginBean> implements TextWatcher {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_register_protocol)
    TextView tvRegisterProtocol;
    @BindView(R.id.cb_register_select_protocol)
    CheckBox cbRegisterSelectProtocol;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.cv_code)
    CodeView cvCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_pwd)
    EditText etPwd;

    private static final String PHONE = "Phone";
    /**
     * 用户手机号码
     */
    private String userPhone;


    /**
     * 是否选中规则
     */
    private boolean choosedRule = true;
    private LocationUtils mLocation;
    //    private SmsCodeBean smsCodeBean;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    public void initData() {
        PermissionUtils.initLocationPermission(this);
        tvTitle.setText("注册");
        userPhone = getIntent().getStringExtra(PHONE);
        setProtocol();
        cbRegisterSelectProtocol.setChecked(true);
        etCode.addTextChangedListener(this);
        etPwd.addTextChangedListener(this);

//        cvCode.loadLast(userPhone, SmsCodeSceneConstant.SIGN_UP);
        cvCode.setListener(new CodeView.CusOnClickListener() {
            @Override
            public void onSuccess(SmsCodeBean mBean) {
//                if (mBean != null) {
//                    smsCodeBean = mBean;
//                }
                Utils.hideInputMethod(RegisterActivity.this);
            }

            @Override
            public void onFail(int code, String msg) {
                Utils.hideInputMethod(RegisterActivity.this);
                showToast(msg);
            }
        });

        //同盾初始化
        FMIdUtils.init(this);

        mLocation = new LocationUtils();

        GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGISTER_PAGE);
    }

    @Override
    public void initListener() {
        cbRegisterSelectProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                choosedRule = b;
                checkData();
            }
        });
    }

    /**
     * 设置协议
     */
    private void setProtocol() {
        String protocol_1 = " 隐私和服务协议";
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
                H5Activity.startActivity(getThis(), String.format(UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT), "", "regist"));
            }
        }, startIndex, startIndex + protocol_1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvRegisterProtocol.setHighlightColor(Color.TRANSPARENT);
        tvRegisterProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        tvRegisterProtocol.setText(spanString);
    }

    public static void start(Context context, String phone) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(PHONE, phone);
        UCardUtil.startActivity(context, intent);
    }


    @OnClick({R.id.tv_next, R.id.iv_title_return, R.id.cv_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_BUTTON_SURE_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGIST_SURE_CLICK);
                finishRegister();
                break;
            case R.id.iv_title_return:
                Utils.hideInputMethod(this);
                finish();
                break;
            case R.id.cv_code:
                GrowingIOUtils.track(UCardConstants.ANDR_BUTTON_SMS_CLICK);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGIST_AMS_CLICK);
                cvCode.requestSmsCode(userPhone, SmsCodeSceneConstant.SIGN_UP);
                break;
        }
    }


    private void finishRegister() {
        final String password = etPwd.getText().toString();
        final String msgCode = etCode.getText().toString();

        if (!Utils.isMobileNO(userPhone)) {
            showToast(getString(R.string.toast_phone_error));
            return;
        }

        if (!PWDUtils.isRightPwd(password)) {
            showToast("密码为8-16位字符，需同时包含字母、数字");
            return;
        }
        if (!choosedRule) {
            showToast("请阅读并同意协议");
            return;
        }
        Utils.hideInputMethod(this);

        mLocation.requestLocation();
        showLoadingDialog();
        FMIdUtils.getFMId(this, new FMIdUtils.CallBack() {
            @Override
            public void callBack(String fmId) {
                RequestMap map = new RequestMap(NetworkAddress.REGISTER);
                map.put("mobile", userPhone);
                map.put("latitude", mLocation.latitude);
                map.put("longitude", mLocation.longitude);
                map.put("password", UserModel.encodePwd(password));
                map.put("fingerPrint", fmId);
                map.put("sms-text", msgCode);
                map.put("wifiInfo", MacUtils.getWifi());
                mNetworkAdapter.request(map, MVPUtils.Method.POST);
            }

            @Override
            public void error(String msg) {
                dismissLoadingDialog();
                showToast("注册失败，请重试");
            }
        });
    }


    @Override
    public void onSuccess(LoginBean bean) {
        dismissLoadingDialog();
        if (bean != null && !TextUtils.isEmpty(bean.accessToken)) {
            bean.save();
            showToast("注册成功");
            GrowingIOUtils.setUserId();

            UCardConstants.UCARD_REGISTER register = new UCardConstants.UCARD_REGISTER();
            register.signup_result = UCardConstants.UCARD_SDA_SUCCEED;
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGISTER, register);

            EventBusUtils.post(BaseNetworkActivity.ACTION_LOGIN);
            finish();
        } else {
            showToast("注册失败");
            UCardConstants.UCARD_REGISTER register = new UCardConstants.UCARD_REGISTER();
            register.signup_result = UCardConstants.UCARD_SDA_FAILED;
            register.error_type = "注册失败";
            GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGISTER, register);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        UCardConstants.UCARD_REGISTER register = new UCardConstants.UCARD_REGISTER();
        register.signup_result = UCardConstants.UCARD_SDA_FAILED;
        register.error_type = msg;
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_REGISTER, register);
        dismissLoadingDialog();
        showToast(msg);
    }


    /**
     * 检测数据
     */
    public void checkData() {
        String password = etPwd.getText().toString();
        String msgCode = etCode.getText().toString();
        boolean isComplete = true;
        if (TextUtils.isEmpty(msgCode)) {
            isComplete = false;
        }
        if (TextUtils.isEmpty(password)) {
            isComplete = false;
        }
        if (!choosedRule) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocation != null) {
            mLocation.removeListener();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GrowingIOUtils.track(UCardConstants.ANDR_REGISTER_PAGE);
    }
}
