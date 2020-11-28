package com.hfax.ucard.bean;

import com.hfax.ucard.bean.BankCardBean;

import java.io.Serializable;
import java.util.List;

/**
 * 借款信息
 *
 * @author SongGuangYao
 * @date 2018/6/25
 */

public class LoanInfoBean  implements Serializable{

    public int loanAmount;//借款总额
    public int periods;//期数
    public int repayPerPeriod;//每期应还
    public String preApplyNo;//预下单号
    public List<BankCardBean> creditCardList;//信用卡列表

    @Override
    public String toString() {
        return "LoanInfoBean{" +
                "loanAmount=" + loanAmount +
                ", periods=" + periods +
                ", repayPerPeriod=" + repayPerPeriod +
                ", creditCardList=" + creditCardList +
                '}';
    }
}
