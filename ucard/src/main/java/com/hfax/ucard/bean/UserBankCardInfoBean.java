package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 王静 on 2018/2/6.
 * 查询用户和银行信息
 */

public class UserBankCardInfoBean implements Serializable {

    private static final long serialVersionUID = -6565571625745593651L;
    public String realName;    //真实姓名
    public String bankLimitInfo; //限额（单笔、单日拼接一起）
    public String idCardTypeNo; //证件类型编号
    public String idCardTypeName;  //证件类型名称
    public String idCardNo;    //证号编号
    public String bankcardNo;  //银行卡号
    public String bankName;  //银行名称
    public String bankcardBin;  //银行bin
    public String mobile;      //预留手机号
    public String isChangeCard;//判断能否换卡（0-不能换卡，1-可以换卡。总资产是否为0）
    public String protocolName;//协议名称
    public String accountType; //账户类型（p2p--网贷账户；fix--网贷账户；空值时默认账户）
    public String onceMoney;  //单笔限额
    public String oneDay;  //单日限额
    public String oneDayDesc; //单日限额描述
    public String onceMoneyDesc;// 单次限额描述
    public String cgStatus; //存管状态（-1：未激活；0：未开通；1：已开通；2：处理中；3：审核中）
    public String checkMsg; // 如果具有强制绑卡的权限，则返回提示语(如果返回值不为空，则展示温馨提示)
    public String cannotChangeMobileMsg; //不可更换预留手机号提示信息(为空时可变更银行卡)

    public List<IdCardTypeListBean> idCardTypeList;

    public static class IdCardTypeListBean implements Serializable {
        private static final long serialVersionUID = 3509886651058890347L;
        public String idCardTypeName; //证件类型名称
        public String idCardTypeNo; //证件类型编码

    }

    /**
     * 是否是存量用户（未激活）
     *
     * @return
     */
    public boolean isInactive() {
        return "-1".equals(cgStatus);
    }
}
