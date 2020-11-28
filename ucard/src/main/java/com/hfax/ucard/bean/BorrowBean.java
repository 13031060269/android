package com.hfax.ucard.bean;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class BorrowBean implements Serializable {
    public static final int STATE_REPAYMENT = 3045;//还款中
    public static final int STATE_WITHDRAW = 3040;//还款中可提现
    public static final int STATE_AFFIRM_NEED = 3025;//确认要款
    public static final int STATE_PAYING = 3030;//打款中
    public static final int STATE_CHECKING = 3010;//审查中
    public static final int STATE_NOPASS = 3015;//审查未通过
    public static final int STATE_RETURN = 3050;//已还清
    public static final int STATE_TIMEOUT = 3020;//超时取消
    public static final int STATE_FAIL = 3035;//打款失败
    private static final long serialVersionUID = -1l;
    public int orderStatus;//订单状态-数字.3010:审核中;3015:审核未通过;3020:超时取消;3025:确认要款;3030:打款中;3035:打款失败;3040:还款中（待提现）;3045:还款中;3050:已还清; ,
    public long amountToBeWithdrawn;//待提现金额(还款中（待提现）有值) ,
    public long applyAmount;//申请金额，单位：分 ,
    public String applyDate;//申请时间，时间戳 ,
    public String applyNo;//借款申请编号 ,
    public boolean isOverdueCheck;//是否逾期 true-是 false-否(还款中（待提现）/还款中有值) ,
    public int isWithdraw;//997 可提现 998 提现中   999 不显示
    public long loanAmount;//借款总金额，单位：分(放款后有值) ,
    public String orderStatusName;//订单状态显示-汉字名称

}
