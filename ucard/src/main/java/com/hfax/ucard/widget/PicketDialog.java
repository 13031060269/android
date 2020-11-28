package com.hfax.ucard.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hfax.ucard.R;
import com.hfax.ucard.widget.wheels.StringScrollPicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择Dialog
 *
 * @author SongGuangyao
 * @date 2018/5/3
 */

public class PicketDialog extends Dialog {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ssp)
    StringScrollPicker ssp;
    @BindView(R.id.tv_type)
    TextView tvType;
    int layoutId;

    /**
     * 当前类型
     */
    private String type;

    private OnClickListen listener;
    private List<String> datas = new ArrayList<>();
    private String defautValue;

    public void setListener(OnClickListen listener) {
        this.listener = listener;
    }

    public PicketDialog(@NonNull Context context, List<String> datas, String type) {
        this(context,datas,type,R.layout.dialog_picket);
    }
    public PicketDialog(@NonNull Context context, List<String> datas, String type,int layoutId) {
        super(context, R.style.BaseCustomAlertDialog);
        this.datas.addAll(datas);
        this.type = type;
        this.layoutId = layoutId;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);
        ButterKnife.bind(this);
        ssp.setData(datas);
        ssp.setSelectedPosition(0);
        tvType.setText(type);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

    }


    /**
     * 设置默认值
     *
     * @param defaultValue
     */
    public void setDefault(String defaultValue) {
        this.defautValue = defaultValue;
    }


    @Override
    public void show() {
        super.show();
        setConfig();

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    /**
     * 设置默认 位置
     */
    private void setConfig() {
        int pCurrent = 0;
        if (!TextUtils.isEmpty(defautValue)) {
            for (int i = 0; i < datas.size(); i++) {
                if (TextUtils.equals(defautValue, datas.get(i))) {
                    pCurrent = i;
                    break;
                }
            }
            ssp.setSelectedPosition(pCurrent);
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                if (listener != null) {
                    listener.onCancel();
                }
                dismiss();
                break;
            case R.id.tv_confirm:
                if (listener != null) {
                    try {
                        listener.onConfirm(datas.get(ssp.getSelectedPosition()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
                break;
        }
    }

    public interface OnClickListen {
        /**
         * 取消
         */
        void onCancel();

        /**
         * 确定
         *
         * @param item
         */
        void onConfirm(String item);
    }

}
