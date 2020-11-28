package com.hfax.ucard.utils;

import android.text.TextUtils;

import com.hfax.ucard.bean.GlobalConfigBean;

import java.util.List;

/**
 * 配置查询
 *
 * @author SongGuangYao
 * @date 2018/6/28
 */

public class ListUtils {
    /**
     * 查询 key
     *
     * @param list  list
     * @param value value
     * @return key
     */
    public static String findKey(List<GlobalConfigBean.Unit> list, String value) {
        if (TextUtils.isEmpty(value) || list == null) {
            return "";
        }
        String key = "";
        for (GlobalConfigBean.Unit unit :
                list) {
            if (value.equals(unit.value)) {
                key = unit.key;
                break;
            }
        }
        return key;
    }

    /**
     * 查询 value
     *
     * @param list list
     * @param key  key
     * @return value
     */
    public static String findValue(List<GlobalConfigBean.Unit> list, String key) {
        if (TextUtils.isEmpty(key) || list == null) {
            return "";
        }
        String value = "";
        for (GlobalConfigBean.Unit unit :
                list) {
            if (key.equals(unit.key)) {
                value = unit.value;
                break;
            }
        }
        return value;
    }
}
