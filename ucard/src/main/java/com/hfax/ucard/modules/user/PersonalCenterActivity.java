package com.hfax.ucard.modules.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.base.MyFragmentActivity;
import com.hfax.ucard.base.MyFragmentActivity2;
import com.hfax.ucard.bean.CacheBean;
import com.hfax.ucard.bean.RealNameAuthenticationBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.loan.CertificationActivity;
import com.hfax.ucard.modules.user.fragment.AccountFragment;
import com.hfax.ucard.modules.user.fragment.PermissionManagerFragment;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.PreventClickUtils;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liuweiping on 2018/5/3.
 */

public class PersonalCenterActivity extends BaseNetworkActivity<RealNameAuthenticationBean> {
    @BindView(R.id.iv_title_return)
    ImageView ivTitleReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    RealNameAuthenticationBean bean;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void initData() {
        tvTitle.setText("个人中心");
        bean = CacheBean.getCache(RealNameAuthenticationBean.class);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        UCardUtil.startActivity(context, intent);
    }

    @OnClick({R.id.iv_title_return, R.id.ll_bankcard, R.id.tv_center_logout, R.id.ll_account, R.id.ll_permission})
    public void onViewClicked(View view) {
        if (PreventClickUtils.canNotClick(view)) return;
        switch (view.getId()) {
            case R.id.tv_center_logout:
                logoutDialog();
                break;
            case R.id.ll_permission:
                MyFragmentActivity2.start(this, PermissionManagerFragment.class);
                break;
            case R.id.iv_title_return:
                finish();
                break;
            case R.id.ll_bankcard:
                if (bean == null) return;
                if (bean.completeIdCard) {
                    MyBankCardActivity.start(this);
                } else {
                    Intent intent = new Intent(this, CertificationActivity.class);
                    intent.putExtra("source", UCardConstants.IDCARD_PERSON_CENTEL);
                    ActivityCallbackUtils.getInstance().putCallback(intent, new ActivityCallbackUtils.Callback() {
                        @Override
                        public void callback(Object o) {
                            if (o instanceof Boolean) {
                                if ((Boolean) o) {
                                    MyBankCardActivity.start(PersonalCenterActivity.this);
                                }
                            }
                        }
                    });
                    UCardUtil.startActivity(this, intent);
                }
                break;
            case R.id.ll_account:
                if (bean == null) return;
                MyFragmentActivity.start(this, AccountFragment.class);
                break;
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        showLoadingDialog();
        mNetworkAdapter.request(NetworkAddress.CHECK_COMPLETE_IDCARD, MVPUtils.Method.GET);
    }

    /**
     * 提示退出登录
     */
    private void logoutDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_logout, null);
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel.logout();
                dialog.dismiss();
                MainActivity.start(PersonalCenterActivity.this);
                finish();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.show();
    }

    @Override
    public void onSuccess(final RealNameAuthenticationBean personalCenterBean) {
        dismissLoadingDialog();
        showContentView();
        if (personalCenterBean == null) return;
        personalCenterBean.saveCache();
        bean = personalCenterBean;
    }

    @Override
    public void onFail(int code, String msg) {
        dismissLoadingDialog();
        showToast(msg);
    }
}
