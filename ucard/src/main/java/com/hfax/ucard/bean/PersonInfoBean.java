package com.hfax.ucard.bean;

import android.text.TextUtils;

import com.hfax.ucard.utils.UCardUtil;

import java.io.Serializable;

/**
 * Created by SongGuangYao on 2018/6/25.
 */

public class PersonInfoBean implements Serializable {


    public String city;
    public String cityCode;
    public String company;
    public String useFor;
    public long education = -1;
    public long income = -1;
    public double latitude;
    public double longitude;
    public long marriage = -1;
    public String name1;
    public String name2;
    public String phone1;
    public String phone2;
    public long profession = -1;
    public long qq = -1;
    public long contactStatus = 0;//是否已经上传过

    /**
     * 查询通讯录上传状态
     *
     * @return true标识也已经上传  false标识未上传
     */
    public boolean getContactStatus() {
        return contactStatus == 1;
    }

    /**
     * 判断该职业是否需要隐藏公司
     *
     * @return false 不需要   true 需要
     */
    public boolean hideCompany() {
        if (profession != -1 && profession != 4 && profession != 99) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "PersonInfoBean{" +
                "city='" + city + '\'' +
                ", company='" + company + '\'' +
                ", education='" + education + '\'' +
                ", income='" + income + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", marriage='" + marriage + '\'' +
                ", name1='" + name1 + '\'' +
                ", name2='" + name2 + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", profession='" + profession + '\'' +
                ", qq='" + qq + '\'' +
                '}';
    }

    /**
     * 检测数据完整性
     */
    public boolean checkData() {
        boolean isFinish = true;
        if (TextUtils.isEmpty(cityCode)) {
            isFinish = false;
        }
        if (profession < 0) {
            isFinish = false;
        }
        if (!hideCompany() && UCardUtil.isEmpty(company)) {
            isFinish = false;
        }
        if (TextUtils.isEmpty(useFor)) {
            isFinish = false;
        }
        if (income < 0) {
            isFinish = false;
        }
        if (qq < 0) {
            isFinish = false;
        }

        if (marriage < 0) {
            isFinish = false;
        }
        if (education < 0) {
            isFinish = false;
        }
        if (TextUtils.isEmpty(phone1)) {
            isFinish = false;
        }
        if (TextUtils.isEmpty(phone2)) {
            isFinish = false;
        }
        if (TextUtils.isEmpty(name1)) {
            isFinish = false;
        }
        if (TextUtils.isEmpty(name2)) {
            isFinish = false;
        }

        return isFinish;
    }
}
