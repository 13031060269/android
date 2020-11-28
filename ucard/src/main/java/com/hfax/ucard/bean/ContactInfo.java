package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by liuweiping on 2018/9/18.
 */

public class ContactInfo implements Serializable{
    public String name;
    public String mobile="";
    public String email;
    public String address;
    public String company;
    public String job;

    @Override
    public String toString() {
        return "ContactInfo{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", company='" + company + '\'' +
                ", job='" + job + '\'' +
                '}';
    }
}
