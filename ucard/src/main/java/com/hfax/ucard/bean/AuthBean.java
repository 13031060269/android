package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 授权bean
 *
 * @author SongGuangYao
 * @date 2018/6/25
 */

public class AuthBean implements Serializable {

    public List<ItemsBean> items;
    public BankCardBean creditCard;//选填信用卡

    public class ItemsBean implements Serializable {
        public String type;//授权类型
        public String status;//授权状态
        public String iconId;//图标id
        public String name;//名称
        public boolean required;//是否必须授权

        @Override
        public String toString() {
            return "ItemsBean{" +
                    "type='" + type + '\'' +
                    ", status='" + status + '\'' +
                    ", iconId='" + iconId + '\'' +
                    ", name='" + name + '\'' +
                    ", required='" + required + '\'' +
                    '}';
        }
    }
}
