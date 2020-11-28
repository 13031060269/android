package com.hfax.ucard.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hfax.ucard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eson on 2017/7/31.
 */

public class SimpleDialog extends AlertDialog implements DialogInterface.OnShowListener {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.cancel)
    TextView tvLeftBtn;
    @BindView(R.id.sure)
    TextView tvRightBtn;
    @BindView(R.id.ll_tow_btn_layout)
    LinearLayout llTowBtnLayout;
    @BindView(R.id.tv_one_btn)
    TextView tvCenterBtn;

    private String title;
    private CharSequence message;
    private String leftBtnName;
    private View.OnClickListener leftBtnListener;
    private String rightBtnName;
    private View.OnClickListener rightBtnListener;
    private String centerBtnName;
    private int centerBtnColor;
    private View.OnClickListener centerBtnListener;


    public SimpleDialog(Context context) {
        super(context, R.style.BaseCustomAlertDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_simple_layout);
        ButterKnife.bind(this);
        setOnShowListener(this);
        setListener();
    }

    private void setListener() {
        tvRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (rightBtnListener != null) {
                    rightBtnListener.onClick(v);
                }
            }
        });
        tvLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (leftBtnListener != null) {
                    leftBtnListener.onClick(v);
                }
            }
        });
        tvCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (centerBtnListener != null) {
                    centerBtnListener.onClick(v);
                }
            }
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setMessage(CharSequence message) {
        this.message = message;
    }

    public void setLeftButton(String name, View.OnClickListener listener) {
        leftBtnName = name;
        leftBtnListener = listener;
    }

    public void setRightButton(String name, View.OnClickListener listener) {
        rightBtnName = name;
        rightBtnListener = listener;
    }

    public void setCenterButton(String name, View.OnClickListener listener) {
        setCenterButton(name, 0, listener);
    }

    public void setCenterButton(String name, int btnColor, View.OnClickListener listener) {
        centerBtnName = name;
        centerBtnColor = btnColor;
        centerBtnListener = listener;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        updateLayout();
    }

    private void updateLayout() {
        if (TextUtils.isEmpty(centerBtnName)) {
            tvCenterBtn.setVisibility(View.GONE);
            tvLeftBtn.setText(leftBtnName);
            tvRightBtn.setText(rightBtnName);
            llTowBtnLayout.setVisibility(View.VISIBLE);
        } else {
            tvCenterBtn.setText(centerBtnName);
            if (centerBtnColor != 0) {
                tvCenterBtn.setTextColor(centerBtnColor);
            }
            tvCenterBtn.setVisibility(View.VISIBLE);
            llTowBtnLayout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(message)) {
            message = "";
        }
        tvMessage.setHighlightColor(Color.TRANSPARENT);
        tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tvMessage.setText(message);
    }
}
