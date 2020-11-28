package com.hfax.ucard.modules.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;

import com.hfax.app.h5.H5Fragment;
import com.hfax.app.utils.ProtocolUtils;
import com.hfax.app.utils.ToastUtils;
import com.hfax.lib.utils.ActivityCallbackUtils;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.MainBean;
import com.hfax.ucard.modules.debug.HostSettingActivity;
import com.hfax.ucard.modules.getui.GetuiAliasUtils;
import com.hfax.ucard.modules.getui.GetuiIntentService;
import com.hfax.ucard.modules.getui.GetuiPushService;
import com.hfax.ucard.modules.user.LoginActivity;
import com.hfax.ucard.modules.user.fragment.UserFragment;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.FMIdUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.PermissionUtils;
import com.hfax.ucard.utils.SPManager;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.hfax.ucard.widget.SimpleDialog;
import com.igexin.sdk.PushManager;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseNetworkActivity<MainBean> implements H5Fragment.H5Callback {
    public static final String KEY_PAGE = "page";
    public static final String PAGE_HOME = "0";
    public static final String PAGE_H5 = "1";
    public static final String PAGE_USER = "2";
    @BindView(R.id.view_pager_main)
    ViewPager view_pager_main;
    @BindView(R.id.rb_main_borrow)
    RadioButton rb_main_borrow;
    @BindView(R.id.rb_main_h5)
    RadioButton rb_main_h5;
    @BindView(R.id.rb_main_mine)
    RadioButton rb_main_mine;
    String curPage = PAGE_HOME;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    H5Fragment h5Fragment = new DiscoverH5();
    Fragment[] fragments = new Fragment[]{new HomeFragment(), h5Fragment, new UserFragment()};

    @Override
    public void initData() {
        Bundle bundle = new Bundle();
        bundle.putString("url", UCardUtil.getH5Url(NetworkAddress.H5_DISCOVER));
        bundle.putBoolean(H5Fragment.KEY_SHOW_NAV, false);
        h5Fragment.setArguments(bundle);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
            } else {
                // 6.0 以上，有权限时，调用 trackInstallation() 触发激活事件。
                trackInstallation();
            }
        } else {
            // 6.0 以下，直接调用 trackInstallation() 触发激活事件。
            trackInstallation();
        }
        PermissionUtils.initSDCardPhoneStatePermission(this);

        view_pager_main.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        view_pager_main.setCurrentItem(Integer.parseInt(PAGE_HOME));
        if (BuildConfig.DEBUG) {
            rb_main_borrow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    HostSettingActivity.start(MainActivity.this);
                    return true;
                }
            });
        }
        processIntent(getIntent());
        FMIdUtils.init(this);
        FMIdUtils.getFMId(this);
        GrowingIOUtils.trackSDA(UCardConstants.UCARD_HOME_PAGE);
        PushManager.getInstance().initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
        PushManager.getInstance().setPrivacyPolicyStrategy(this.getApplicationContext(), true);
    }

    /**
     * 记录激活事件
     */
    private void trackInstallation() {
        try {
            JSONObject properties = new JSONObject();
            //这里示例 DownloadChannel 记录下载商店的渠道(下载渠道)。如果需要多个字段来标记渠道包，请按业务实际需要添加。
            properties.put("channel", Utils.CHANNEL);
            //记录激活事件、渠道追踪，这里激活事件取名为 AppInstall。
            SensorsDataAPI.sharedInstance().trackInstallation("AppInstall", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.rb_main_borrow, R.id.rb_main_mine, R.id.rb_main_h5})
    public void onViewClicked(View view) {
        try {
            switch (view.getId()) {
                case R.id.rb_main_borrow:
                    goTo(PAGE_HOME);
                    break;
                case R.id.rb_main_h5:
                    goTo(PAGE_H5);
                    break;
                case R.id.rb_main_mine:
                    goTo(PAGE_USER);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {

    }

    public void goTo(@NonNull String page) {
        switch (page) {
            default:
            case PAGE_HOME:
                rb_main_borrow.setChecked(true);
                view_pager_main.setCurrentItem(Integer.parseInt(PAGE_HOME));
                break;
            case PAGE_H5:
                rb_main_h5.setChecked(true);
                view_pager_main.setCurrentItem(Integer.parseInt(PAGE_H5));
                break;
            case PAGE_USER:
                if (UserModel.isLogin()) {
                    rb_main_mine.setChecked(true);
                    view_pager_main.setCurrentItem(Integer.parseInt(PAGE_USER));
                } else {
                    goTo(curPage);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    ActivityCallbackUtils.getInstance().putCallback(intent, new ActivityCallbackUtils.Callback() {
                        @Override
                        public void callback(Object obj) {
                            if (UserModel.isLogin()) {
                                goTo(PAGE_USER);
                            }
                        }
                    });
                    UCardUtil.startActivity(this, intent);
                    return;
                }
                break;

        }
        curPage = page;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        trackInstallation();
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i]) && !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        // 只显示一次
                        if (SPManager.getBoolean(SPManager.SP_KEY_PROMISES, true)) {
                            // 勾选了不再提示拒绝
                            SimpleDialog dialog = new SimpleDialog(this);
                            dialog.setCenterButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            dialog.setMessage("未授权SD卡读写权限，为了提供完整的服务，建议您在手机设置中进行相应授权");
                            dialog.show();
                            SPManager.putBoolean(SPManager.SP_KEY_PROMISES, false);
                        }
                    } else {
                        // 手动拒绝
                        LogUtil.e("手动拒绝");
                    }
                }
            }
        }

    }

    public static void start(Context activity) {
        start(activity, PAGE_HOME);
    }

    public static void start(Context activity, String page) {
        UCardUtil.startActivity(activity, new Intent(activity, MainActivity.class).putExtra(KEY_PAGE, page));
    }

    private void processIntent(Intent intent) {
        String page = intent.getStringExtra(KEY_PAGE);
        if (!TextUtils.isEmpty(page)) {
            intent.removeExtra(KEY_PAGE);
            try {
                goTo(page);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Uri uri = intent.getData();
        if (uri != null) {
            intent.setData(null);
            Intent startIntnt = ProtocolUtils.parseProtocol(this, uri);
            if (startIntnt != null) startActivity(startIntnt);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntent().putExtras(intent);
        processIntent(intent);
    }

    @Override
    public void onSuccess(MainBean mainBean) {

    }

    @Override
    public void onFail(int code, String msg) {

    }

    private long firstBackTime;

    @Override
    public void onBackPressed() {
//        if (curPage == PAGE_H5) {
//            h5Fragment.onBackPressed();
//            return;
//        }
        backPress();
    }

    @Override
    protected boolean isStartSupportGestureFinish() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetuiAliasUtils.updateGeTuiAlias();
    }

    @Override
    public void finishH5() {

    }

    @Override
    public void backPress() {
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            showToast("再按一次退出程序");
            firstBackTime = System.currentTimeMillis();
        } else {
            super.finish();
            overridePendingTransition(0, R.anim.out_to_bottom);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.getToast().clear();
    }

    public static class DiscoverH5 extends H5Fragment{
        @Override
        protected void onLoad() {
            super.onLoad();
            if (BuildConfig.DEBUG){
                resetUrl(UCardUtil.getH5Url(NetworkAddress.H5_DISCOVER));
            }
        }
    }
}
