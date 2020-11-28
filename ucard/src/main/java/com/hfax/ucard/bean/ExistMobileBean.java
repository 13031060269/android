package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * 手机号是否为平台注册用户
 * Created by SongGuangYao on 2018/6/20.
 */

public class ExistMobileBean implements Serializable {

    public boolean exist;

    @Override
    public String toString() {
        return "ExistMobileBean{" +
                "exist=" + exist +
                '}';
    }
}
