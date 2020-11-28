package com.hfax.ucard.bean;

import android.text.TextUtils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.utils.mvp.modle.UserModel;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class UserBean extends CacheBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public NoticeBean notice;
    public RepaymentSummary repaymentSummary;

    public static class NoticeBean implements Serializable {
        private static final long serialVersionUID = -1l;
        public String applyNo = "";//借款编号 ,
        public String notice = "";//提示信息 ,
        public int status;//状态: 0-无提示；1-未完成实名认证；2-逾期
    }

    public static class RepaymentSummary implements Serializable {
        private static final long serialVersionUID = -1l;
        public long paymentMonth;//本月应还，单位：分 ,
        public long paymentTotal;//待还本息，单位：分
    }
}
