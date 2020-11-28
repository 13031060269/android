package com.hfax.ucard.modules.user.fragment;

import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkFragment;

import butterknife.BindView;

/**
 * 卡券
 */
public class MsgCenterFragment extends BaseNetworkFragment {
    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_msg_center;
    }

    @Override
    protected void initListener() {
        tv_title.setText("消息中心");
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }
}
