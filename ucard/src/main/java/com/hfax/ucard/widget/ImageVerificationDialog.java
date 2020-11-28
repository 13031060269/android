package com.hfax.ucard.widget;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.ImageCodeBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

/**
 * @author SongGuangYao
 */

public class ImageVerificationDialog extends Dialog implements View.OnClickListener {
    private final BaseNetworkActivity activity;
    private TextView tvDialogCancle, tvDialogSure;
    private ImageView ivDialogImgCode;
    private EditText etDialogCode;
    private CusOnClickListener listener;
    private ImageCodeBean imageCodeBean;


    public ImageVerificationDialog(BaseNetworkActivity activity) {
        super(activity, R.style.BaseCustomAlertDialog);
        this.activity = activity;
    }

    public void setSureBtnOnclickListener(CusOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_verification);
        tvDialogCancle = (TextView) findViewById(R.id.tv_dialog_imagecode_cancle);
        tvDialogSure = (TextView) findViewById(R.id.tv_dialog_imagecode_sure);
        ivDialogImgCode = (ImageView) findViewById(R.id.iv_dialog_imagecode);
        etDialogCode = (EditText) findViewById(R.id.et_dialog_code);
        tvDialogSure.setOnClickListener(this);
        ivDialogImgCode.setOnClickListener(this);
        tvDialogCancle.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        etDialogCode.setText("");
        if (imageCodeBean != null && !TextUtils.isEmpty(imageCodeBean.img)) {
            Bitmap bitmap = Utils.StringToBitmap(imageCodeBean.img);
            if (bitmap != null) {
                ivDialogImgCode.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_dialog_imagecode:
                requestImgCode();
                break;
            case R.id.tv_dialog_imagecode_cancle:
                dismiss();
                break;
            case R.id.tv_dialog_imagecode_sure:
                if (listener != null) {
                    listener.onClick(imageCodeBean);
                }
                dismiss();
                break;
        }
    }

    public String getInputCode() {
        return etDialogCode.getText().toString();
    }

    public void setImgCode(String base64Str) {
        Bitmap bitmap = Utils.StringToBitmap(base64Str);
        if (bitmap != null) {
            ivDialogImgCode.setImageBitmap(bitmap);
        }

    }


    public void requestImgCode() {
        Utils.hideInputMethod(activity);
        if (!isShowing()) {
            activity.showLoadingDialog();
        }
        activity.getmNetworkAdapter().request(NetworkAddress.IMGCODE_URL, MVPUtils.Method.GET, new SimpleViewImpl<ImageCodeBean>() {
            @Override
            public void onSuccess(ImageCodeBean bean) {
                activity.dismissLoadingDialog();
                imageCodeBean = bean;
                if (isShowing()) {
                    setImgCode(imageCodeBean.img);
                } else {
                    show();
                }
            }

            @Override
            public void onFail(int code, String msg) {
                activity.dismissLoadingDialog();
            }
        });
    }

    public interface CusOnClickListener {

        void onClick(ImageCodeBean imageCodeBean);
    }
}
