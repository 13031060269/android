package com.hfax.ucard.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.hfax.lib.AppConfig;
import com.hfax.ucard.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WXEntryActivity.this;
        api = WXAPIFactory.createWXAPI(this, AppConfig.WEIXIN_API_KEY, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);

    }

    @Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {

        String result ;
        Resources res = getResources();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = res.getString(R.string.share_errcode_success);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = res.getString(R.string.share_errcode_deny);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = res.getString(R.string.share_errcode_cancel);
                break;
            default:
                result = res.getString(R.string.share_errcode_unknown);
                break;
        }

        if(!TextUtils.isEmpty(result)){
            Toast.makeText(mContext, result , Toast.LENGTH_LONG).show();
        }
        finish();
        overridePendingTransition(0, 0);
	}


}
