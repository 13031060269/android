package com.hfax.ucard.bean;

import com.hfax.ucard.utils.UCardUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by eson on 2017/7/26.
 */

public class BorrowDetails implements Serializable {
    private static final long serialVersionUID = -1l;
    public int amountToBeWithdrawn;// (integer, optional): 待提现金额(还款中（待提现）有值) ,
    public int applyAmount;//(integer, optional): 申请金额，单位：分 ,
    public String applyDate;//(integer, optional): 申请时间，时间戳 ,
    public String applyNo;//(string, optional): 借款申请编号 ,
    public int confirmLeftTime;// (integer, optional): 确认要款截止时间,剩余秒数(确认要款有值) ,
    public FailBarContentVo failBarContentVo;//(FailBarContentVo, optional): 一笔失败显示条内容vo(还款中待提现、还款中有值，为null就不显示) ,
    public int isWithdraw;//997 可提现 998 提现中   999 不显示
    public int loanAmount;//(integer, optional): 借款总金额，单位：分(放款后有值) ,
    public List<BankCardBean> loanCardVos;//(Array[BankCardInfoVo], optional): 代还信用卡信息(确认要款有值) ,
    public List<OrderInfoVo> orderInfoVos;//(Array[OrderInfoVo], optional): 拆单详情vo(非必传) ,
    public int orderStatus;//(integer, optional): 订单状态-数字.3010:审核中;3015:审核未通过;3020:超时取消;3025:确认要款;3030:打款中;3035:打款失败;3040:还款中（待提现）;3045:还款中;3050:已还清; ,
    public String orderStatusName;//(string, optional): 订单状态显示-汉字名称 ,
    public int periods;//(integer, optional): 借款期限 ,
    public BankCardBean repayCardVo;//(BankCardInfoVo, optional): 还款银行卡信息(非必传) ,
    public int repayMonth;//(integer, optional): 月还款，单位：分(非必传) ,
    public List<RepayPlanVo> repayPlanVos;//(Array[RepayPlanVo], optional): 还款计划vo(非必传) ,
    public boolean splitCheck;//(boolean, optional): 是否是拆单的申请/true/false(非必传) ,
    public int surplusMonth;//(integer, optional): 剩余未还金额，单位：分(非必传) ,
    public int surplusPeriods;//(integer, optional): 剩余未还期数(非必传)
    public String repayTypeName;//还款方式
    public int orderType;//1含有p2p  2纯小贷
    public List<BankCardBean> repayCardVos;
    public List<Coupon> coupon;
    public int[] approvePeriods;//风控返回的审批期限
    public String repayNoticeMsg;//还款计划页面,还款金额提示条,时光分期api必传

    public class FailBarContentVo implements Serializable {
        private static final long serialVersionUID = -1l;
        public String applyDate;//(integer, optional): 申请时间，时间戳 ,
        public int loanAmount;//(integer, optional): 借款金额，单位：分 ,
        public int periods;//(integer, optional): 借款期限 ,
        public int repayMonth;//(integer, optional): 月还款，单位：分
    }

    public class Coupon implements Serializable {
        private static final long serialVersionUID = -1l;
        public String couponNo;//优惠券编号
        public int couponType;//优惠券类型 1:减息券
        public long derateAmount;//减免金额 单位:分
        public int feeType;//(减免类型
        List<DerateDetail> derateDetail;//减免详细( 还款时原样返回即可 )
    }

    public class DerateDetail implements Serializable {
        private static final long serialVersionUID = -1l;
        long derateAmount;//可抵扣金额 单位:分 优惠券可减免当前费用类型的金额
        int feeType;//减免类型
    }

    public class OrderInfoVo implements Serializable {
        private static final long serialVersionUID = -1l;
        public int loanAmount;//(integer, optional): 借款金额，单位：分 ,
        public int periods;//(integer, optional): 借款期限 ,
        public int repayMonth;//(integer, optional): 月还款，单位：分
    }

    public void initRepayCardVo() {
        if (!UCardUtil.isCollectionEmpty(repayCardVos)) {
            int position = 0;
            for (int i = 0; i < repayCardVos.size(); i++) {
                if (repayCardVos.get(i).currentChoose) {
                    position = i;
                    break;
                }
            }
            repayCardVo = repayCardVos.get(position);
        }
    }

    public class RepayPlanVo implements Serializable {
        private static final long serialVersionUID = -1l;
        public String loanId;//(string, optional): 放款订单编号-隐身 ,
        public int overdueTotal;//(integer, optional): 含逾期罚息字段-显示 ,
        public int period;//(integer, optional): 期数-排序-隐身 ,
        public int planGuaranteeFee;// (integer, optional): 应还担保费，单位：分-隐身 ,
        public int planInterest;//(integer, optional): 应还利息，单位：分-隐身 ,
        public int planLoanAfterFee;//(integer, optional): 应还贷后管理费，单位：分 （对应产品需求中，其他费用中包应还贷后管理费的，必传；不包含的传 0）-隐身 ,
        public int planOverdueFee;//(integer, optional): 应还逾期利息，单位：分-隐身 ,
        public int planPenaltyFee;//(integer, optional): 应还罚息，单位：分-隐身 ,
        public int planPrincipal;//(integer, optional): 应还本金，单位：分-隐身 ,
        public int planServiceFee;//(integer, optional): 应还咨询服务费，单位：分-隐身 ,
        public int planRepayTotal;//(integer, optional): 应还总额
        public int raiseMode;//(integer, optional): 放贷类型:-1-未知,1-小贷,2-P2P-隐身 ,
        public String repayPlanDate;//(string, optional): 计划还款日期-显示 ,
        public int repayStatus;//(integer, optional): 还款状态 1000-还款中 2000-已还款 3000-逾期中 1-还款处理中,
        public String repayStatusName;//(string, optional): 还款状态汉字-显示 ,
        public int repayTotal;//(integer, optional): 还款总额字段-显示
        public boolean selective;//(boolean, optional): 是否可选true/false;为true则当期显示可选择按钮 ,
        public List<RepayPlanVo> splitRepayPlan;//拆单时,总单有值即子单详细还款计划,其下面子单为null;非拆单值为null
        public boolean isCheck;//合并订单的chckbox是否选中
        public int deRateTotal;//减免总额,单位：分
        public int deRatePrincipal;//减免本金金额，单位：分
        public int deRateInterest;//减免利息金额，单位：分
        public int deRateServiceFee;//减免服务费金额，单位：分
        public int deRatePenaltyFee;//减免罚息金额 ，单位：分
        public int deRateOverdueInterest;//减免逾期利息金额，单位：分
        public int deRateGuaranteeFee;//减免担保费金额，单位：分
        public int deRateInsuranceFee;//减免保费，单位：分
        public int actuaInsuranceFee;//实还保费，单位：分
        public int planInsuranceFee;//应还保费，单位：分
        public int planTechnologyServiceFee;//应还技术服务费，单位：分
        public int actualTechnologyServiceFee;//实还技术服务费，单位：分
        public int deRateTechnologyServiceFee;//减免技术服务费，单位：分
        public int deRateLoanAfterFee;//减免贷后管理费金额，单位：分 （对应产品需求中，其他费用中包应还贷后管理费的，必传；不包含的传 0）
        public int actualGuaranteeFee;// (integer, optional): 实担保费（对应产品需求中，其他费用中包含担保费的，必传；不包含的传 0）单位：分 ,
        public int actualInterest;// (integer, optional): 实际还款利息单位：分 ,
        public int actualLoanAfterFee;// (integer, optional): 实还贷后管理费（对应产品需求中，其他费用中包应还贷后管理费的，必传；不包含的传 0）单位：分 ,
        public int actualOverdueInterest;// (integer, optional): 实际还款逾期利息（其他费用中包含逾期利息的，必传；不包含的传 0） 单位：分 ,
        public int actualPenaltyFee;// (integer, optional): 实际还款罚息（减免后罚息） 单位：分 ,
        public int actualPrincipal;// (integer, optional): 实际还款本金单位：分 ,
        public int actualServiceFee;// (integer, optional): 实还服务费（对应产品需求中，其他费用中包含服务费的，必传；不包含的传 0）单位：分 ,
        public Coupon coupon;

        public List<RepayPlanVo> getRepays() {
            List<RepayPlanVo> beans = splitRepayPlan;
            if (UCardUtil.isCollectionEmpty(beans)) {
                beans = Collections.singletonList(this);
            }
            return beans;
        }

        public List<RepayPlanVo> getPostParameter(List<Coupon> coupon) {
            List<RepayPlanVo> beans = getRepays();
            if (!UCardUtil.isCollectionEmpty(beans) && !UCardUtil.isCollectionEmpty(coupon)) {
                for (RepayPlanVo re : beans) {
                    re.coupon = coupon.get(0);
                }
            }
            return beans;
        }

        public int getRepayTotal() {
            int result = 0;
            List<RepayPlanVo> repays = getRepays();
            for (RepayPlanVo bean : repays) {
                result += bean.repayTotal;
            }
            return result;
        }

    }

}
