package com.hfax.ucard.bean;

import android.text.TextUtils;

import com.hfax.facelib.util.Util;
import com.hfax.ucard.utils.UCardUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by eson on 2017/7/26.
 */

public class GlobalConfigBean extends CacheBean implements Serializable {
    private static final long serialVersionUID = -1l;
    public List<Unit> education;//学历
    public List<Unit> profession;//职业
    public List<Unit> income;//收入
    public List<Unit> useFor = new ArrayList<>();//用途
    public List<Unit> marriage;//婚姻状况
    public List<Unit> creditBank;//信用卡银行列表
    public List<Unit> debitBank;//借记卡银行列表
    public List<CityUnit> city;//城市列表
    public List<Unit> repayment;//首页配置
    public List<Unit> periods;//分期配置
    public Map<String, List<CityUnit>> map;
    HomeConfig homeConfig;
    public long lastUpdateTimeStamp;

    public class Unit implements Serializable {
        private static final long serialVersionUID = -1l;
        public String key;
        public String value;
    }

    public class CityUnit implements Serializable {
        public String value;//北京市
        public String parent;//父类
        public String key;//自己编码

        @Override
        public String toString() {
            return "CityUnit{" +
                    "value='" + value + '\'' +
                    ", parent='" + parent + '\'' +
                    ", key='" + key + '\'' +
                    '}';
        }
    }

    @Override
    protected boolean isUserDiff() {
        return false;
    }

    /**
     * 获取城市
     *
     * @param key 对应key
     * @return 子集
     */
    public List<CityUnit> getCity(String key) {

        if (UCardUtil.isCollectionEmpty(city) || TextUtils.isEmpty(key)) {
            return null;
        }
        if (map == null) {
            map = new HashMap<>();
            //获取父类
            for (CityUnit u : city) {
                List<CityUnit> list1 = map.get(u.parent);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                    map.put(u.parent, list1);
                }
                list1.add(u);
            }
        }
        return map.get(key);
    }

    public class HomeConfig implements Serializable {
        private static final long serialVersionUID = -1l;
        public int minAmount = 400000;
        public int maxAmount = 5000000;
        public int amountStep = 100000;
        public int defaultAmount = 1000000;

        private HomeConfig(List<Unit> list) {
            if (UCardUtil.isCollectionEmpty(list)) return;
            for (Unit unit : list) {
                try {
                    Field field = HomeConfig.class.getField(unit.key);
                    field.set(this, Integer.parseInt(unit.value));
                } catch (Exception e) {
                }

            }
        }
    }

    public HomeConfig getHomeConfig() {
        if (homeConfig == null) {
            homeConfig = new HomeConfig(this.repayment);
        }
        return homeConfig;
    }

    public List<String> getPeriods() {
        List<String> monthAbout = new ArrayList<>(Arrays.asList("6个月", "9个月", "12个月"));
        if (!UCardUtil.isCollectionEmpty(periods)) {
            monthAbout.clear();
            for (Unit unit : periods) {
                monthAbout.add(unit.key + "个月");
            }
        }
        return monthAbout;
    }


    @Override
    public void saveCache() {
        GlobalConfigBean cache = getCache(getClass());
        if (cache == null || cache.lastUpdateTimeStamp < this.lastUpdateTimeStamp) {
            super.saveCache();
        }
    }
}
