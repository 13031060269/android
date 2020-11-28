package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by 王静 on 2017/7/31.
 * 绑定银行卡（验证银行卡卡号）
 */

public class BankCardCheckBean implements Serializable {
    private static final long serialVersionUID = 2224471726880874756L;
    public String usability;  //银行卡是否可以绑定（0： 不可绑定，1:可绑定）
    public String bankId;     //代扣银行编号
    public String bankName;   //发卡行名称
    public String dctype;     //借贷类型 新加 银行卡类型(0:借记卡 1:信用卡 2:预付卡 3:准贷记卡)
    public String limitMsg;  //银行卡限额
    public String bankcardNo;  //银行卡号
}
