package com.hfax.ucard.bean;

import java.io.Serializable;


public class RestRepayBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public int actualGuaranteeFee;// (integer, optional): 实担保费（对应产品需求中，其他费用中包含担保费的，必传；不包含的传 0）单位：分 ,
    public int actualInterest;// (integer, optional): 实际还款利息单位：分 ,
    public int actualLoanAfterFee;// (integer, optional): 实还贷后管理费（对应产品需求中，其他费用中包应还贷后管理费的，必传；不包含的传 0）单位：分 ,
    public int actualOverdueInterest;// (integer, optional): 实际还款逾期利息（其他费用中包含逾期利息的，必传；不包含的传 0） 单位：分 ,
    public int actualPenaltyFee;// (integer, optional): 实际还款罚息（减免后罚息） 单位：分 ,
    public int actualPrincipal;// (integer, optional): 实际还款本金单位：分 ,
    public int actualServiceFee;// (integer, optional): 实还服务费（对应产品需求中，其他费用中包含服务费的，必传；不包含的传 0）单位：分 ,
    public String loanId;// (string, optional): 借款订单id ,
    public int period;// (integer, optional): 第几期还款 ,
    public int raiseMode ;//(integer, optional): 募集模式，1：小贷模式，2：P2P模式 ,
    public int repayTotal;// (integer, optional): 实际还款总额
    public int deRateTotal;//减免总额,单位：分
    public int deRatePrincipal;//减免本金金额，单位：分
    public int deRateInterest;//减免利息金额，单位：分
    public int deRateServiceFee;//减免服务费金额，单位：分
    public int deRatePenaltyFee;//减免罚息金额 ，单位：分
    public int deRateOverdueInterest;//减免逾期利息金额，单位：分
    public int deRateGuaranteeFee;//减免担保费金额，单位：分
    public int deRateLoanAfterFee;//减免贷后管理费金额，单位：分 （对应产品需求中，其他费用中包应还贷后管理费的，必传；不包含的传 0）
    public RestRepayBean(BorrowDetails.RepayPlanVo repayPlanVo){
        actualGuaranteeFee=repayPlanVo.planGuaranteeFee;
        actualInterest=repayPlanVo.planInterest;
        actualLoanAfterFee=repayPlanVo.planLoanAfterFee;
        actualOverdueInterest=repayPlanVo.planOverdueFee;
        actualPenaltyFee=repayPlanVo.planPenaltyFee;
        actualPrincipal=repayPlanVo.planPrincipal;
        actualServiceFee=repayPlanVo.planServiceFee;
        loanId=repayPlanVo.loanId;
        period=repayPlanVo.period;
        raiseMode=repayPlanVo.raiseMode;
        repayTotal=repayPlanVo.repayTotal;
        deRateTotal=repayPlanVo.deRateTotal;
        deRatePrincipal=repayPlanVo.deRatePrincipal;
        deRateInterest=repayPlanVo.deRateInterest;
        deRateServiceFee=repayPlanVo.deRateServiceFee;
        deRatePenaltyFee=repayPlanVo.deRatePenaltyFee;
        deRateOverdueInterest=repayPlanVo.deRateOverdueInterest;
        deRateGuaranteeFee=repayPlanVo.deRateGuaranteeFee;
        deRateLoanAfterFee=repayPlanVo.deRateLoanAfterFee;
    }
}
