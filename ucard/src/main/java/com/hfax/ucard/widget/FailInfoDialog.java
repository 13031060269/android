package com.hfax.ucard.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提交失败
 *
 * @author SongGuangyao
 * @date 2018/5/4
 */

public class FailInfoDialog extends Dialog {
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.tv_next)
    TextView tvNext;
    private String content;
    private String title;
    private String btnStr;

    private OnClickListen listener;

    public FailInfoDialog(@NonNull Context context, String title, String content) {
        super(context, R.style.BaseCustomAlertDialog);
        this.content = content;
        this.title = title;
    }

    public FailInfoDialog(@NonNull Context context, String title, String content, String btnStr) {
        super(context, R.style.BaseCustomAlertDialog);
        this.content = content;
        this.title = title;
        this.btnStr = btnStr;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fail_info);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        } else {
            tvContent.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = rlContent.getLayoutParams();
            layoutParams.height = layoutParams.height - Utils.dip2px(getContext(), 26);
            rlContent.setLayoutParams(layoutParams);
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(btnStr)) {
            tvNext.setText(btnStr);
        }
    }

    @OnClick(R.id.tv_next)
    public void onViewClicked() {
        if (listener != null) {
            listener.onClick();
        }
        dismiss();
    }


    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    public void setListener(OnClickListen listener) {
        this.listener = listener;
    }

    public interface OnClickListen {
        /**
         * 点击
         */
        void onClick();
    }
}
