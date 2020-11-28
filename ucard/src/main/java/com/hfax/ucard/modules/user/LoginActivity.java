package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.lib.HfaxConstants;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.base.MyFragmentActivity2;
import com.hfax.ucard.bean.ExistMobileBean;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.modules.user.dialog.LoginContractDialog;
import com.hfax.ucard.modules.user.fragment.SMSFragment;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.YunPianUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录引导
 *
 * @author SongGuangyao
 * @date 2018/4/23
 */

public class LoginActivity extends BaseNetworkActivity<ExistMobileBean> {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.tv_goto_pwd)
    TextView tv_goto_pwd;
    RequestMap requestMap = new RequestMap(NetworkAddress.GET_LOGIN_CODE);

    private String phone;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_login;
    }

    public static void start(Context context) {
        UCardUtil.startActivity(context, new Intent(context, LoginActivity.class));
    }


    @Override
    public void initData() {
        ivReturn.setImageResource(R.drawable.lock_point_close);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || !Utils.isMobile(s.toString())) {
                    tvNext.setEnabled(false);
                } else {
                    tvNext.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //清理缓存
        LoginBean.clear();
    }

    @OnClick({R.id.iv_return, R.id.tv_next, R.id.tv_goto_pwd})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.tv_goto_pwd:
                String phone = etPhone.getText().toString();
                if (!Utils.isMobile(phone)) {
                    phone = null;
                }
                LoginByPwdActivity.start(this, phone);
                break;
            case R.id.iv_return:
                finish();
                break;
            case R.id.tv_next:
                GrowingIOUtils.track(UCardConstants.ANDR_TEL_NEXT_CHANGE);
                GrowingIOUtils.trackSDA(UCardConstants.UCARD_TEL_NEXT_CLICK);
                flitPhone();
                break;
        }
    }

    /**
     * 过滤手机号码
     */
    private void flitPhone() {
        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入手机号码");
            return;
        } else if (!Utils.isMobile(phone)) {
            showToast("手机号格式错误");
            return;
        }
        YunPianUtils.getYunPian().requestYunPian(this,phone+YunPianUtils.SCENE_LOGIN, new DataChange<Map<String, String>>() {
            @Override
            public void onChange(Map<String, String> m) {
                if (!UCardUtil.isEmpty(m)) {
                    showLoadingDialog();
                    requestMap.clear();
                    requestMap.putAll(m);
                    requestMap.put("mobile", phone);
                    requestMap.put("check-mobile", true);
                    mNetworkAdapter.request(requestMap, MVPUtils.Method.POST);
                }
            }
        });

    }

    private void goSMS(boolean isNew) {
        Bundle bundle = new Bundle();
        bundle.putString(SMSFragment.getPHONE(), phone);
        bundle.putBoolean(SMSFragment.getCHECKED(), isNew);
        MyFragmentActivity2.start(this, SMSFragment.class, bundle);
    }


    @Override
    protected void onRestart() {
        //如果用户已经登录，则关闭
        if (UserModel.isLogin()) {
            setResult(HfaxConstants.COMMON_RESULT_CODE);
            finish();
        }
        super.onRestart();
    }

    @Override
    public void onSuccess(ExistMobileBean bean) {
        dismissLoadingDialog();
        if (bean.exist) {//存在
            goSMS(false);
        } else {//不存在
            new LoginContractDialog(getThis(), new DataChange<Boolean>() {
                @Override
                public void onChange(Boolean aBoolean) {
                    showLoadingDialog();
                    requestMap.put("check-mobile", false);
                    mNetworkAdapter.request(requestMap, MVPUtils.Method.POST, new SimpleViewImpl<ExistMobileBean>() {
                        @Override
                        public void onSuccess(ExistMobileBean existMobileBean) {
                            dismissLoadingDialog();
                            goSMS(true);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            dismissLoadingDialog();
                            showToast(msg);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void finish() {
        Utils.hideInputMethod(this);
        super.finish();
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }
}
