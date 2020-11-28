package com.hfax.ucard.widget.codes;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;


/**
 * 验证码倒计时器
 *
 * @author SongGuangyao
 * @date 2018/4/24
 */
public class CodeCountDownTimer extends CountDownTimer {
    private TextView btn_djs;
    private String msgText = "秒后重发";
    private String finishText = "重新获取验证码";
    /**
     * 倒计时未完成时的时间
     */
    private Long continueTimer;

    //是否完成倒计时
    private boolean isFinish = false;

    public CodeCountDownTimer(long millisInFuture, TextView btn) {
        super(millisInFuture * 1000, 1000);
        btn_djs = btn;
    }

    /**
     * 设置持续时间
     *
     * @param continueMillis
     */
    public void setContinueTime(Long continueMillis) {
        this.continueTimer = continueMillis;
        isFinish = false;
    }

    @Override
    public void onTick(long l) {
        if (isFinish) {
            return;
        }
        if (continueTimer != null) {
            continueTimer = continueTimer - 1;
            if (continueTimer >= 1) {
                btn_djs.setClickable(false);
                btn_djs.setText(continueTimer + msgText);
            } else {
                isFinish = true;
                continueTimer = null;
                btn_djs.setText(finishText);
                btn_djs.setEnabled(true);
                cancel();
            }
        } else {
            btn_djs.setEnabled(false);
            btn_djs.setText(l / 1000 + msgText);
        }
    }

    @Override
    public void onFinish() {
        this.continueTimer = null;
        btn_djs.setText(finishText);
        btn_djs.setEnabled(true);
    }

}
