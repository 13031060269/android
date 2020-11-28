package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.ModifyLoginPasswordBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PWDUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 修改登录密码
 *
 * @author SongGuangyao
 * @date 2018/4/23
 */

public class ModifyLoginPwdActivity extends BaseNetworkActivity<ModifyLoginPasswordBean> implements TextWatcher {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_modify_oldPassword)
    EditText etModifyOldPassword;
    @BindView(R.id.et_modify_newPassword)
    EditText etModifyNewPassword;
    @BindView(R.id.tv_modify_next)
    TextView tvModifyNext;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_modify_login_password;
    }

    @Override
    public void initData() {
        tvTitle.setText("修改登录密码");

        etModifyNewPassword.addTextChangedListener(this);
        etModifyOldPassword.addTextChangedListener(this);
    }


    @OnClick({R.id.iv_title_return, R.id.tv_modify_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                Utils.hideInputMethod(ModifyLoginPwdActivity.this);
                finish();
                break;
            case R.id.tv_modify_next:
                modifyPwd();
                break;
        }
    }

    /**
     * 修改密码
     */
    private void modifyPwd() {
        String newPwd = etModifyNewPassword.getText().toString();
        String oldPwd = etModifyOldPassword.getText().toString();

        if (PWDUtils.isRightPwd(newPwd)) {
            if (oldPwd.equals(newPwd)) {
                showToast("新旧密码不能相同");
                return;
            }
            Utils.hideInputMethod(ModifyLoginPwdActivity.this);
            showLoadingDialog();
            RequestMap map = new RequestMap(NetworkAddress.UPDATE_LOGIN_PWD);
            map.put("oldPassword", UserModel.encodePwd(oldPwd));
            map.put("newPassword", UserModel.encodePwd(newPwd));
            mNetworkAdapter.request(map, MVPUtils.Method.POST);
        } else {
            showToast("密码为8-16位字符，需同时包含字母、数字");
        }
    }

    @Override
    public void onSuccess(ModifyLoginPasswordBean bean) {
        dismissLoadingDialog();
        showToast("修改成功");
        finish();
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyLoginPwdActivity.class);
        UCardUtil.startActivity(context, intent);
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
        String newPwd = etModifyNewPassword.getText().toString();
        String oldPwd = etModifyOldPassword.getText().toString();

        if (TextUtils.isEmpty(oldPwd)) {
            isComplete = false;
        }
        if (TextUtils.isEmpty(newPwd)) {
            isComplete = false;
        }
        tvModifyNext.setEnabled(isComplete);
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
