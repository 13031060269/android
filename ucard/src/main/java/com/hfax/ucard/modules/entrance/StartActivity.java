package com.hfax.ucard.modules.entrance;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.hfax.lib.utils.SPUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.CacheBean;
import com.hfax.ucard.bean.GlobalConfigBean;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.UCardUtil;

public class StartActivity extends BaseNetworkActivity {

    private String SIGN = "8ceed84e897105ea42a4e74167fb48a7";
    private static final long delayMillis = 2000;

    private void goNext(Uri data) {
        String preVersion = SPUtils.getString(getApplicationContext(), UCardConstants.KEY_VERSION, "");
        Intent intent;
        if (!Utils.APP_VERSION.equals(preVersion)) {
            SPUtils.putString(getApplicationContext(), UCardConstants.KEY_VERSION, Utils.APP_VERSION);
            CacheBean.clear(GlobalConfigBean.class);
            intent = new Intent(StartActivity.this, GuideActivity.class).setData(data);
        } else {
            intent = new Intent(StartActivity.this, MainActivity.class).setData(data);
        }
        UCardUtil.startActivity(this, intent);
        finish();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_start;
    }

    @Override
    public void initData() {
        if (Utils.isOriginSign(getApplicationContext(), SIGN)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Uri data = getIntent().getData();
                    if (data == null) {
                        String dataString = getIntent().getDataString();
                        if (!TextUtils.isEmpty(dataString)) {
                            data = Uri.parse(dataString);
                        }
                    }
                    goNext(data);
                }
            }, delayMillis);
        } else {
            finish();
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
