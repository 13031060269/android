package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Banner广告页
 * Created by SongGuangYao on 2018/11/5.
 */

public class BannerBean implements Serializable {
    public String imageUrl;
    public String redirectLink;//跳转链接
    public String resourceKey;//资源key
    public int showOrder;//显示顺序
    public String title;//标题
}
