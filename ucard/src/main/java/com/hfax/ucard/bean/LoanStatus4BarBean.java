package com.hfax.ucard.bean;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class LoanStatus4BarBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public String applyNo;//借款申请编号 ,
    public String actionNotice;//提醒信息2（首页订单状态提示条） ,
    public int status;//提醒信息（首页订单状态提示条） ,
    public String notice;// 提醒信息1（首页订单状态提示条） ,
    public Coupon coupon;

    public static class Coupon implements Serializable {
        public String fingerprint;
        public int amount;
        public String info;
    }

}
