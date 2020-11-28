package com.hfax.ucard.widget.codes;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.hfax.lib.utils.Utils;
import com.hfax.ucard.R;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.bean.LastCodeBean;
import com.hfax.ucard.bean.SmsCodeBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.YunPianUtils;
import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

import java.util.Map;

/**
 * 验证码View
 *
 * @author SongGuangYao
 */
public class CodeView extends AppCompatTextView {

    private CusOnClickListener listener;
    //验证码倒计时器
    private CodeCountDownTimer codeCountDownTimer;

    private BaseNetworkActivity mActivity;

    ImageCodeDialog imageCodeDialog;

    private String phone;
    private String type;

    public CodeView(Context context) {
        super(context);
        init(context);
    }

    public CodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mActivity = (BaseNetworkActivity) context;
        setTextColor(context.getResources().getColorStateList(R.color.color_selector_sms));
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        setPadding(Utils.dip2px(context, 8), Utils.dip2px(context, 4), Utils.dip2px(context, 8), Utils.dip2px(context, 4));
        setText("获取验证码");
        setBackgroundResource(R.drawable.selector_verify_round);
        codeCountDownTimer = new CodeCountDownTimer(60, this);
    }

    /**
     * 加载上次是否剩余时间
     *
     * @param phone 手机号码
     * @param type  类型
     */
    public void loadLast(String phone, String type) {
        RequestMap map = new RequestMap(NetworkAddress.LAST_CODE);
        map.put("mobile", phone);
        map.put("sms-type", type);
        mActivity.getmNetworkAdapter().request(map, MVPUtils.Method.GET, new SimpleViewImpl<LastCodeBean>() {
            @Override
            public void onSuccess(LastCodeBean lastCodeBean) {
                if (lastCodeBean.countDownTime > 0) {
                    codeCountDownTimer.setContinueTime(lastCodeBean.countDownTime);
                    codeCountDownTimer.start();
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void requestCode(String phone, String type) {
        requestCode(phone, type, null, null);
    }

    /**
     * 请求验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     */
    private void requestCode(String phone, String type, String captchaId
            , String captchaText) {
        if (!Utils.isMobileNO(phone)) {
            mActivity.showToast(mActivity.getString(R.string.toast_phone_error));
            return;
        }
        Utils.hideInputMethod(mActivity);
        final RequestMap map = new RequestMap(NetworkAddress.GET_RESETPWD_CODE);
        map.put("sms-type", type);
        map.put("mobile", phone);
        if (!TextUtils.isEmpty(captchaId) && !TextUtils.isEmpty(captchaText)) {
            map.put("captcha-id", captchaId);
            map.put("captcha-text", captchaText);
        }

        YunPianUtils.getYunPian().requestYunPian(mActivity, phone + YunPianUtils.SCENE_FORGET, new DataChange<Map<String, String>>() {
            @Override
            public void onChange(Map<String, String> stringStringMap) {
                if (stringStringMap != null) {
                    mActivity.showLoadingDialog();
                    map.putAll(stringStringMap);
                    mActivity.getmNetworkAdapter().request(map, MVPUtils.Method.POST, new SimpleViewImpl<SmsCodeBean>() {
                        @Override
                        public void onSuccess(SmsCodeBean mBean) {
                            mActivity.dismissLoadingDialog();
                            mActivity.showToast(getResources().getString(R.string.get_sms_success_dialog));
                            if (listener != null) {
                                listener.onSuccess(mBean);
                            }
                            if (imageCodeDialog != null) {
                                imageCodeDialog.dismiss();
                            }
                            start();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            mActivity.dismissLoadingDialog();
                            switch (code) {
                                case NetworkAddress.CODE_ERROR_PIC:
                                case NetworkAddress.CODE_ERROR_PIC_OVER:
                                    mActivity.showToast(msg);
                                    getImgCode();
                                    break;
                                case NetworkAddress.CODE_ERROR_MSG_SEND:
                                case NetworkAddress.CODE_ERROR_MSG_OVER:
                                case NetworkAddress.CODE_ERROR_MSG:
                                case NetworkAddress.CODE_ERROR_CHECK:
                                    mActivity.showToast(msg);
                                    if (listener != null) {
                                        listener.onFail(code, msg);
                                    }
                                    break;
                                case NetworkAddress.CODE_ERROR_MSG_INPUT:
                                    getImgCode();
                                    break;
                                default:
                                    mActivity.showToast(msg);
                                    break;
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     * 开始倒计时
     */
    private void start() {
        codeCountDownTimer = new CodeCountDownTimer(60, this);
        codeCountDownTimer.start();
    }

    /**
     * 设置监听
     *
     * @param listener 监听
     */
    public void setListener(CusOnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 请求短信验证码
     *
     * @param phone 手机号
     * @param type  类型
     */
    public void requestSmsCode(String phone, String type) {
        if (!Utils.isMobile(phone)) {
            mActivity.showToast("请输入正确的手机号");
            return;
        }
        this.phone = phone;
        this.type = type;
        requestCode(phone, type);
    }

    private void getImgCode() {
//        if (imageCodeDialog == null) {
//            imageCodeDialog = new ImageCodeDialog(mActivity);
//            imageCodeDialog.setSureBtnOnclickListener(new ImageCodeDialog.CusOnClickListener() {
//                @Override
//                public void onClick(ImageCodeBean imageCodeBean) {
//                    requestCode(phone, type, imageCodeBean.captchaId, imageCodeDialog.getInputCode());
//                }
//            });
//        }
//        imageCodeDialog.requestImgCode();
    }

    public interface CusOnClickListener {
        void onSuccess(SmsCodeBean mBean);

        void onFail(int code, String msg);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (codeCountDownTimer != null) {
            codeCountDownTimer.cancel();
        }
    }
}
