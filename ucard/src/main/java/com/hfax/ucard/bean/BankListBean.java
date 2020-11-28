package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;


/**
 * Created by eson on 2017/7/26.
 */

public class BankListBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public List<BankCardBean> creditCardList;
    public List<BankCardBean> platformCardList;
    public BankCardBean debitCard;
}
