package com.hfax.ucard.modules.debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author :li ChuanWu on 2018/3/2
 * Blog  ：http://blog.csdn.net/lsyz0021/
 */
public class HostSettingBean implements Serializable {
    private static final long serialVersionUID = -3265228409102663869L;
    public String name;
    public String url;

    public HostSettingBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static List<HostSettingBean> getAddressUrl() {
        List<HostSettingBean> url = new ArrayList<>();
        url.add(new HostSettingBean("qa2", "https://qa2.beneucard.com/"));
        url.add(new HostSettingBean("qa3", "https://qa3.beneucard.com/"));
        url.add(new HostSettingBean("qa4", "https://qa4.beneucard.com/"));
        url.add(new HostSettingBean("qa5", "https://qa5.beneucard.com/"));
        url.add(new HostSettingBean("qa6", "https://qa6.beneucard.com/"));
        url.add(new HostSettingBean("qa7", "https://qa7.beneucard.com/"));
        url.add(new HostSettingBean("qa8", "https://qa8.beneucard.com/"));
        url.add(new HostSettingBean("n-", "https://n-m.beneucard.com/"));
        url.add(new HostSettingBean("准生产", "https://prem.beneucard.com/"));
        url.add(new HostSettingBean("生产", "https://m.beneucard.com/"));
        return url;
    }
}
