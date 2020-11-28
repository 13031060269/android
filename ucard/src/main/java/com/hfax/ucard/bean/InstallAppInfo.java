package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by liuweiping on 2018/9/18.
 */

public class InstallAppInfo  implements Serializable{
    public String name;
    public String packageName;

    @Override
    public String toString() {
        return "InstallAppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
