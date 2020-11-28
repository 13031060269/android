package com.hfax.ucard.bean;

import android.text.TextUtils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class BankCardBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public static final String TYPE_DEBIT = "DEBIT";//储蓄卡
    public static final String TYPE_CREDIT = "CREDIT";//信用卡
    public static final String TYPE_CHANGE_DEBIT = "CHANGE_CREDIT";//更换储蓄卡
    public static final String TYPE_PLATFORM = "PLATFORM";//平台卡
    public static final String TYPE_CHANGE_PLATFORM = "CHANGE_PLATFORM";//更换平台卡
    public String bankName;//银行名称
    public String bankCode;//银行编码
    public String cardNo;//银行卡号
    public String cardType;//卡类型 DEBIT：储蓄卡 CREDIT：信用卡
    public boolean currentChoose;//是否选中
    public String reserveMobile;//银行手机号


}
