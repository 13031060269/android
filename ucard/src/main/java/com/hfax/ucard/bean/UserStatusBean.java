package com.hfax.ucard.bean;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class UserStatusBean implements Serializable {
    private static UserStatusBean bean;
    private static final long serialVersionUID = -1l;
    public int creditAuth;//信用授权完成状态。 0=未完成或者失效，1=已完成.
    public int face;//完成状态。 0=未完成或者失效，1=已完成. ,
    public int idCard;//身份证完成状态。 0=未完成或者失效，1=已完成. ,
    public int profile;//个人信息完成状态。 0=未完成或者失效，1=已完成.
    public loanStatus4Button loanStatus4Button;//订单状态

    public static class loanStatus4Button {
        private static final long serialVersionUID = -1l;
        public String info;//提示信息
        public int status;// 订单状态. ,
        public int toast;// 是否toast.(0=不toast,1=toast)
    }

    public static UserStatusBean getBean() {
        return bean;
    }

    public static void save(UserStatusBean userStatusBean) {
        bean = userStatusBean;
    }


    /**
     * 是否完成face授权
     *
     * @return
     */
    public boolean getFace() {
        return face == 1;
    }

    /**
     * 信用授权完成状态
     *
     * @return
     */
    public boolean getCreditAuth() {
        return creditAuth == 1;
    }

    /**
     * 身份证完成状态
     *
     * @return
     */
    public boolean getIdCard() {
        return idCard == 1;
    }

    /**
     * 个人信息完成状态
     *
     * @return
     */
    public boolean getProfile() {
        return profile == 1;
    }


    /**
     * 状态更新
     */
    public void setFace() {
        this.face = 1;
    }

    /**
     * 状态更新
     */
    public void setIdCard() {
        this.idCard = 1;
    }

    /**
     * 状态更新
     */
    public void setProfile() {
        this.profile = 1;
    }

    /**
     * 状态更新
     */
    public void setCreditAuth() {
        this.creditAuth = 1;
    }
}
