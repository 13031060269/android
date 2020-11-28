package com.hfax.ucard.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eson on 2017/8/10.
 */

public class PWDUtils {
    /**
     * 是否是相同的字符
     *
     * @param numOrStr
     * @return
     */
    public static boolean equalStr(String numOrStr) {
        boolean flag = true;
        char str = numOrStr.charAt(0);
        for (int i = 0; i < numOrStr.length(); i++) {
            if (str != numOrStr.charAt(i)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 是否是连续的数字
     *
     * @param numOrStr
     * @return
     */
    public static boolean isOrderNumber(String numOrStr) {
        boolean flag = true;//如果全是连续数字返回true
        boolean isNumeric = true;//如果全是数字返回true
        for (int i = 0; i < numOrStr.length(); i++) {
            if (!Character.isDigit(numOrStr.charAt(i))) {
                isNumeric = false;
                break;
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (int i = 0; i < numOrStr.length(); i++) {
                if (i > 0) {//判断如123456
                    int num = Integer.parseInt(numOrStr.charAt(i) + "");
                    int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
            if (!flag) {
                flag = true;
                for (int i = 0; i < numOrStr.length(); i++) {
                    if (i > 0) {//判断如654321
                        int num = Integer.parseInt(numOrStr.charAt(i) + "");
                        int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
                        if (num != num_) {
                            flag = false;
                            break;
                        }
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 是否是特殊字符开头
     *
     * @param pwd
     * @return
     */
    public static boolean isStartWithSpecialChar(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            return !pwd.matches("^[0-9a-zA-Z].*$");
        }
        return false;
    }

    /**
     * 是否含有特殊字符
     *
     * @param pwd
     * @return
     */
    public static boolean containsSpecialChar(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            return !pwd.matches("^[0-9a-zA-Z]+$");
        }
        return false;
    }


    /**
     * 是否含有英文字母
     *
     * @param pwd
     * @return
     */
    public static boolean isContainsEnglishChar(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            String regex = ".*[a-zA-Z]+.*";
            Matcher m = Pattern.compile(regex).matcher(pwd);
            return m.matches();
        }
        return false;
    }


    /**
     * 是否含有数字
     *
     * @param pwd
     * @return
     */
    public static boolean isContainsNumber(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            String regex = ".*[0-9]+.*";
            Matcher m = Pattern.compile(regex).matcher(pwd);
            return m.matches();
        }
        return false;
    }


    /**
     * 是否为8-16位置 数字字母集合
     *
     * @param pwd
     * @return
     */
    public static boolean isRightPwd(String pwd) {
        return !TextUtils.isEmpty(pwd) && pwd.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,16}$");
    }
}
