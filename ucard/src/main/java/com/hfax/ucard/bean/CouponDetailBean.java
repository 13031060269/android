package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;


/**
 * Created by eson on 2017/7/26.
 */

public class CouponDetailBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public List<CouponDetail> unused;//未使用优惠券列表 (无数据时传空数组)
    public List<CouponDetail> used;//已使用优惠券列表 (无数据时传空数组)
    public List<CouponDetail> expired;//已过期优惠券列表 (无数据时传空数组)

    public static class CouponDetail implements Serializable {
        private static final long serialVersionUID = -1l;
        public String couponNo;//优惠券编号
        public int type;//优惠券类型 1:减息券2:免息券
        public long loanAmount;//需满xxx借款金额 单位:分
        public long denomination;//type为1时必传, 优惠券面额 单位:分
        public long duration;//type为2时必传,免息天数 单位:天
        public String startTime;//优惠券有效期起始时间
        public String typeLabel;//优惠券类型 减息券,免息券
        public String endTime;//优惠券有效期结束时间
        public int status;//优惠券状态 100:未使用(包含已冻结),200:已使用, 300:已过期
        public List<String> description;//优惠券文案
    }
}
