package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.hfax.app.utils.EventBusUtils;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.FMIdUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.LocationUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.MacUtils;
import com.hfax.ucard.utils.PermissionUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录页面
 *
 * @author SongGuangyao
 * @date 2018/4/23
 */
public class LoginByPwdActivity extends BaseNetworkActivity<LoginBean> {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    /**
     * 传递的手机号码
     */
    private String phone;

    private static final String PHONE = "Phone";
    private LocationUtils mLocation;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_login_bypwd;
    }


    @Override
    public void initData() {
        GrowingIOUtils.trackSDA(UCardConstants.PASSWORD_LOGIN_PAGE);
        PermissionUtils.initLocationPermission(this);
        phone = getIntent().getStringExtra(PHONE);
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
            etPhone.setSelection(phone.length());
        }
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(etPwd.getText().toString())) {
                    tvLogin.setEnabled(false);
                } else {
                    tvLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(etPhone.getText().toString())) {
                    tvLogin.setEnabled(false);
                } else {
                    tvLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FMIdUtils.init(this);
        mLocation = new LocationUtils();


    }

    public static void start(Context context, String phone) {
        Intent intent = new Intent(context, LoginByPwdActivity.class);
        intent.putExtra(PHONE, phone);
        UCardUtil.startActivity(context, intent);
    }


    @OnClick({R.id.iv_title_return, R.id.tv_login, R.id.tv_forgetPwd})
    public void onViewClicked(View view) {
        Utils.hideInputMethod(this);
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.tv_login:
                GrowingIOUtils.track(UCardConstants.ANDR_LOGIN_BOTTON_CLICK);
                checkData();
                break;
            case R.id.tv_forgetPwd:
                Intent intent = new Intent(LoginByPwdActivity.this, ForgetLoginPwdActivity.class);
                String phone = etPhone.getText().toString();
                if (Utils.isMobile(phone)) {
                    intent.putExtra("phone", phone);
                }
//                ActivityCallbackUtils.getInstance().putCallback(intent, new ActivityCallbackUtils.Callback() {
//                    @Override
//                    public void callback(Object o) {
//                        if (o instanceof Boolean) {
//                            if ((Boolean) o) {
//                                finish();
//                            }
//                        }
//                    }
//                });
                UCardUtil.startActivity(LoginByPwdActivity.this, intent);
                break;
        }
    }

    /**
     * 检测数据
     */
    private void checkData() {
        final String phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入手机号码");
            return;
        } else if (!Utils.isMobile(phone)) {
            showToast("手机号格式错误");
            return;
        }
        final String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入登录密码");
            return;
        }
        if (pwd.length() < 8 || pwd.length() > 16) {
            showToast("密码为8-16位字符");
            return;
        }
        mLocation.requestLocation();
        showLoadingDialog();
        FMIdUtils.getFMId(this, new FMIdUtils.CallBack() {
            @Override
            public void callBack(String fmId) {
                RequestMap map = new RequestMap(NetworkAddress.LOGIN);
                map.put("fingerPrint", fmId);
                map.put("longitude", mLocation.longitude);
                map.put("latitude", mLocation.latitude);
                map.put("mobile", phone);
                map.put("password", UserModel.encodePwd(pwd));
                map.put("wifiInfo", MacUtils.getWifi());
                mNetworkAdapter.request(map, MVPUtils.Method.POST);
            }

            @Override
            public void error(String msg) {
                dismissLoadingDialog();
                showToast("登录失败，请重试");
            }
        });
    }


    @Override
    public void onSuccess(LoginBean loginBean) {

        dismissLoadingDialog();
        if (loginBean != null && !TextUtils.isEmpty(loginBean.accessToken)) {
            GrowingIOUtils.trackSDA(UCardConstants.PASSWORD_LOGIN_CLICK, new UCardConstants.LOGIN_RESULT());
            loginBean.save();
            showToast("登录成功");
            GrowingIOUtils.setUserId();

            EventBusUtils.post(BaseNetworkActivity.ACTION_LOGIN);
            finish();
        } else {
            showToast("密码错误，请重新输入");
            UCardConstants.LOGIN_RESULT login_result = new UCardConstants.LOGIN_RESULT();
            login_result.login_result = UCardConstants.UCARD_SDA_FAILED;
            login_result.error_type = "返回的accessToken为空";
            GrowingIOUtils.trackSDA(UCardConstants.PASSWORD_LOGIN_CLICK, login_result);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        UCardConstants.LOGIN_RESULT login_result = new UCardConstants.LOGIN_RESULT();
        login_result.login_result = UCardConstants.UCARD_SDA_FAILED;
        login_result.error_type = msg;
        GrowingIOUtils.trackSDA(UCardConstants.PASSWORD_LOGIN_CLICK, login_result);
        dismissLoadingDialog();
        showToast(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocation != null) {
            mLocation.removeListener();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (UserModel.isLogin()) {
            finish();
        }
    }
}
