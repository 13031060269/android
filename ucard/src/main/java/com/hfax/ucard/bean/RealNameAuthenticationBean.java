package com.hfax.ucard.bean;

import java.io.Serializable;


/**
 * Created by eson on 2017/7/26.
 */

public class RealNameAuthenticationBean extends CacheBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public boolean completeIdCard;// 是否完成身份认证 ,
    public String idNo;// 身份证脱敏 ,
    public String name;// 姓名脱敏
}
