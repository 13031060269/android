package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * 身份证bean
 *
 * @author SongGuangYao
 * @date 2018/5/22
 */

public class IDCardBean  implements Serializable{
    public String idNo;
    public String name;

    @Override
    public String toString() {
        return "IDCardBean{" +
                "idNo='" + idNo + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
