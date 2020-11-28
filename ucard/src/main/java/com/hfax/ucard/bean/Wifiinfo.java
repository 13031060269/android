package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by liuweiping on 2018/9/18.
 */

public class Wifiinfo implements Serializable {
    public String ssid;
    public String bssid;

    @Override
    public String toString() {
        return "Wifiinfo{" +
                "ssid='" + ssid + '\'' +
                ", bssid='" + bssid + '\'' +
                '}';
    }
}
