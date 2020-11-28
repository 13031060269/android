package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;


public class HomeWindowStatusBean implements Serializable {
    private static final long serialVersionUID = -1;
    public int windowStatus;//是否需要弹窗(1-需要 2-不需要)
    public List<Contract> items;//合同列表

    public static class Contract implements Serializable {
        private static final long serialVersionUID = -1;
        public String templateId;//合同模板号
        public String contractName;//合同名称
    }
}

