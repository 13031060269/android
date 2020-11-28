package com.hfax.ucard.modules.loan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.modules.borrow.BorrowDetailsActivity;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.utils.Constants.UCardConstants;
import com.hfax.ucard.utils.UCardUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 状态
 *
 * @author SongGuangYao
 */
public class StatusActivity extends BaseNetworkActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_back_home)
    TextView tvBackHome;
    @BindView(R.id.tv_next)
    TextView tvNext;

    private StatusAction statusAction;
    private String data;//数据

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_status;
    }

    @Override
    public void initData() {
        String status = getIntent().getStringExtra("status");
        if (status != null) {
            switch (status) {
                case UCardConstants.STATUS_LOAN:
                    statusAction = new LoanStatus();
                    data = getIntent().getStringExtra("data");
                    break;
                case UCardConstants.STATUS_BACK:
                    statusAction = new BackStatus();
                    break;
            }
            if (statusAction != null) {
                statusAction.init();
            }
        }
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFail(int code, String msg) {

    }


    public static void start(Context context, String status) {
        start(context, status, "");
    }

    public static void start(Context context, String status, String data) {
        Intent intent = new Intent(context, StatusActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("data", data);
        UCardUtil.startActivity(context, intent);
    }


    @OnClick({R.id.iv_return, R.id.tv_next, R.id.tv_back_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                if (statusAction != null) {
                    statusAction.doBack();
                }
                break;
            case R.id.tv_next:
                if (statusAction != null) {
                    statusAction.doNext();
                }
                break;
            case R.id.tv_back_home:
                if (statusAction != null) {
                    statusAction.doBack();
                }
                break;
        }
    }


    interface StatusAction {
        void doNext();

        void init();

        void doBack();
    }

    /**
     * 我要借款
     */
    class LoanStatus implements StatusAction {

        @Override
        public void doNext() {
            BorrowDetailsActivity.start(StatusActivity.this, data);
            finish();
        }

        @Override
        public void init() {
            tvTitle.setText("提交成功");
            tvContent.setText("您的借款申请已提交成功，\n" + "正在飞速审核中");
            tvNext.setText("查看申请进度");
            tvBackHome.setText("返回首页");
            tvBackHome.setVisibility(View.VISIBLE);
        }

        @Override
        public void doBack() {
            finish();
        }
    }

    /**
     * 还款
     */
    class BackStatus implements StatusAction {

        @Override
        public void doNext() {
            finish();
        }

        @Override
        public void init() {
            tvTitle.setText("还款提交成功");
            tvContent.setText("还款申请提交成功，处理结果\n" + "将以短信形式通知");
            tvNext.setText("查看详情");
        }

        @Override
        public void doBack() {
            finish();
        }
    }
}
