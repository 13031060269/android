package com.hfax.ucard.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 核对个人信息弹框
 *
 * @author SongGuangyao
 * @date 2018/4/27
 */

public class ConfirmPersonInfoDialog extends Dialog {

    @BindView(R.id.iv_mobile)
    ImageView ivMobile;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_forget_next)
    TextView tvForgetNext;

    private OnClickListen listener;

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;

    public ConfirmPersonInfoDialog(@NonNull Context context, String title, String content) {
        super(context, R.style.BaseCustomAlertDialog);
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_personinfo);
        ButterKnife.bind(this);
        getWindow().getDecorView().setPadding(
                Utils.dip2px(getContext(), 32), 0, Utils.dip2px(getContext(), 32), 0);
        init();
    }


    private void init() {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnClickListener(OnClickListen listener) {
        this.listener = listener;
    }

    @OnClick({R.id.tv_cancel, R.id.tv_forget_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                if (listener != null) {
                    listener.cancel();
                }
                dismiss();
                break;
            case R.id.tv_forget_next:
                if (listener != null) {
                    listener.next();
                }
                dismiss();
                break;
        }
    }

    public interface OnClickListen {
        /**
         * 取消
         */
        void cancel();

        /**
         * 确认
         */
        void next();
    }
}
