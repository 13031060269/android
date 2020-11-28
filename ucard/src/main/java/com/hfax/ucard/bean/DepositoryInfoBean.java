package com.hfax.ucard.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by 王静 on 2018/2/9.
 */

public class DepositoryInfoBean implements Serializable {
    private static final long serialVersionUID = -7822239036702581571L;

    /**
     * 页面来源
     */
    public static final String KEY_FROM = "key_from";
    /**
     * 来自网贷
     */
    public static final String P2P = "p2p";
    /**
     * 来自定期
     */
    public static final String FIX = "fix";
    /**
     * 来自零钱
     */
    public static final String NON = "non";

    /**
     * 处理中状态
     */
    public static final String PROCESSING = "2";
    /**
     * 审核中状态
     */
    public static final String INREVIEW = "3";

    public String isBankCardBind; //银行卡是否绑定（0-未绑卡；1-已绑卡）
    public String isP2pRegister;  // p2p是否开通(-1-未激活，0-未开通，1-已开通，2-处理中)
    public String isFixRegister;  // fix是否开通(-1-未激活，0-未开通，1-已开通，2-处理中)
    public String cgStatus;  // 存管是否开通（全渠道）存管开通状态(-1-未激活，0-未开通，1-已开通，2、处理中,3,审核中：外籍用户在PC注册还没通过人工审核，然后在app查看)

    public boolean isBankCardBind() {
        return "1".equals(isBankCardBind);
    }

    /**
     * 已经开通P2P账户
     *
     * @return
     */
    public boolean isP2pRegister() {
        return "1".equals(isP2pRegister);
    }

    /**
     * 已经开通定期账户
     *
     * @return
     */
    public boolean isFixRegister() {
        return "1".equals(isFixRegister);
    }

    /**
     * 是否开通存管
     * true：已开通
     * false：未开通
     *
     * @return
     */
    public boolean isDredgeDeposit() {
        return TextUtils.equals("1", cgStatus);
    }

    /**
     * 两个账号都未开通
     *
     * @return
     */
    public boolean isAllNotAvailable() {
        if (TextUtils.isEmpty(isP2pRegister) || TextUtils.isEmpty(isFixRegister)) {
            return false;
        }
        try {
            return Long.parseLong(isP2pRegister) <= 0 && Long.parseLong(isFixRegister) <= 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

}
