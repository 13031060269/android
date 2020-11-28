package com.hfax.ucard.bean;

import com.hfax.ucard.bean.Wifiinfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongGuangYao on 2018/9/21.
 */

public class CurrentWifiInfo implements Serializable{
    public String currentBssid;
    public String currentSsid;
    public List<Wifiinfo> wifiList;


}
