package com.hfax.ucard.base;

import android.content.res.Configuration;
import android.os.Build;

import com.hfax.app.BaseActivity;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleNetworkAdapter;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleView;

import org.greenrobot.eventbus.Subscribe;

public abstract class BaseNetworkActivity<T> extends BaseActivity implements SimpleView<T> {
    protected SimpleNetworkAdapter mNetworkAdapter = new SimpleNetworkAdapter(this);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNetworkAdapter.onDesDroy();
    }

    public SimpleNetworkAdapter getmNetworkAdapter() {
        return mNetworkAdapter;
    }

    @Override
    public void initListener() {
    }

    protected void onLoad() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        onLoad();
    }

    @Override
    protected void onReLoad() {
        super.onReLoad();
        onLoad();
    }

    @Subscribe
    public void logState(String state) {
        switch (state) {
            case ACTION_LOGIN:
                onLogin();
                break;
            case ACTION_LOGOUT:
                onLogout();
                break;
            default:
        }
    }

    ;

    protected void onLogin() {

    }

    protected void onLogout() {

    }

    protected BaseNetworkActivity getThis() {
        return this;
    }

    @Override
    final protected void onUserLogout() {
        super.onUserLogout();
    }

    @Override
    final protected void onUserLogin() {
        super.onUserLogin();
    }

    @Override
    public void showToast(CharSequence msg) {
//        super.showToast(msg);
        UCardUtil.showToast(this, msg);
    }

    {//禁用系统字体调节
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = new Configuration();
            configuration.fontScale = 1;
            try {
                applyOverrideConfiguration(configuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
