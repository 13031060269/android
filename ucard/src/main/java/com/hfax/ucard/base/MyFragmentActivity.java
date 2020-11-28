package com.hfax.ucard.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hfax.app.BaseActivity;
import com.hfax.ucard.utils.UCardUtil;


/**
 * Created by liuweiping on 2018/5/3.
 */

public class MyFragmentActivity extends BaseNetworkActivity {
    ViewGroup root;
    static final String FRAGEMEN = "fragemen";

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    public void initData() {
        try {
            Class<? extends Fragment> clazz = (Class<? extends Fragment>) getIntent().getSerializableExtra(FRAGEMEN);
            Fragment fragment = clazz.newInstance();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(root.getId(), fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View getLayoutView() {
        root = new FrameLayout(this);
        root.setId(UCardUtil.generateViewId());
        return root;
    }

    @Override
    public void initListener() {

    }

    public static void start(Activity activity, Class<? extends Fragment> fragment) {
        start(activity, fragment, null);
    }

    public static void start(Activity activity, Class<? extends Fragment> fragment, Bundle bundle) {
        Intent intent = new Intent(activity, MyFragmentActivity.class);
        intent.putExtra(FRAGEMEN, fragment);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        UCardUtil.startActivity(activity, intent);
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
