package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by Vincent on 2018/6/29.
 */


public class CollectResult  implements Serializable {

    String token;
    String sign;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
