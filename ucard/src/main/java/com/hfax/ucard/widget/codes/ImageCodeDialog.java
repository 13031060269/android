package com.hfax.ucard.widget.codes;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfax.app.BaseActivity;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.bean.ImageCodeBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;


/**
 * 图片验证码Dialog
 *
 * @author SongGuangYao
 */
public class ImageCodeDialog extends Dialog implements View.OnClickListener {
    private final BaseActivity activity;
    private TextView tvDialogCancle, tvDialogSure;
    private ImageView ivDialogImgCode;
    private EditText etDialogCode;
    private CusOnClickListener listener;
    private ImageCodeBean imageCodeBean;


//    public ImageCodeDialog(BaseActivity activity) {
//        super(activity, R.style.BaseCustomAlertDialog);
//        this.activity = activity;
//    }

    public ImageCodeDialog(BaseActivity activity, ImageCodeBean imageCodeBean) {
        super(activity, R.style.BaseCustomAlertDialog);
        this.activity = activity;
        this.imageCodeBean = imageCodeBean;
    }

    public void setSureBtnOnclickListener(CusOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
    public void dismiss() {
        super.dismiss();
        Log.e("info---", "dismiss-----");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_dialog_imagecode:
                requestImgCode();
                break;
            case R.id.tv_dialog_imagecode_cancle:
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
                break;
            case R.id.tv_dialog_imagecode_sure:
                String inputCode = getInputCode();
                if (TextUtils.isEmpty(inputCode)) {
                    activity.showToast("请输入图片验证码");
                    return;
                }
                dismiss();
                if (listener != null) {
                    listener.onClick(imageCodeBean, inputCode);
                }
                break;
        }
    }

    private String getInputCode() {
        return etDialogCode.getText().toString();
    }

    private void setImgCode(String base64Str) {
        Bitmap bitmap = Utils.StringToBitmap(base64Str);
        if (bitmap != null) {
            ivDialogImgCode.setImageBitmap(bitmap);
        }

    }


    /**
     * 刷新图片验证码
     */
    private void requestImgCode() {
        Utils.hideInputMethod(activity);
        new SimplePresent().request(new RequestMap(NetworkAddress.IMGCODE_URL), activity, MVPUtils.Method.GET, new SimpleViewImpl<ImageCodeBean>() {
            @Override
            public void onSuccess(ImageCodeBean bean) {
                imageCodeBean = bean;
                setImgCode(imageCodeBean.img);

            }

            @Override
            public void onFail(int code, String msg) {
                activity.showToast(msg);
            }
        });
    }

    public interface CusOnClickListener {
        void onClick(ImageCodeBean imageCodeBean, String text);

        void onCancel();
    }
}
