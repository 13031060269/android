package com.hfax.ucard.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SongGuangYao on 2018/11/5.
 */

public class BannerModel extends CacheBean implements Serializable {
    public int totalCount;
    public List<BannerBean> bannerDetailVos;
}
