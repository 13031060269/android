package com.hfax.ucard.modules.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.BaseActivity;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.RealNameAuthenticationBean;
import com.hfax.ucard.utils.UCardUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class RealNameAuthenticationActivity extends BaseNetworkActivity {
    @BindView(R.id.iv_title_return)
    ImageView ivTitleReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_idNo)
    TextView tv_idNo;
    private static final String key_name = "name";
    private static final String key_idNo = "idNo";

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_realname_authentication;
    }

    @Override
    public void initData() {
        tvTitle.setText("实名认证");
        tv_name.setText(getIntent().getStringExtra(key_name));
        tv_idNo.setText(getIntent().getStringExtra(key_idNo));
    }

    @Override
    public void initListener() {

    }

    public static void start(Context context, String name, String idNo) {
        UCardUtil.startActivity(context, new Intent(context, RealNameAuthenticationActivity.class)
                .putExtra(key_name, name)
                .putExtra(key_idNo, idNo));
    }

    @OnClick({R.id.iv_title_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_title_return:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
