package com.hfax.ucard.utils;

import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by liuweiping on 2018/9/13.
 */

public class PreventClickUtils {
    private static final int interval = 1000;
    private static Map<Long, Long> _times = new LinkedHashMap<Long, Long>() {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > 10;
        }
    };

    public static boolean canNotClick(View view) {
        Long key = 0L;
        if (view != null) {
            key = (long) view.getId();
        }
        Long value = _times.get(key);
        long thisTime = System.currentTimeMillis();
        if (value == null || thisTime - value > interval) {
            _times.put(key, thisTime);
            return false;
        }
        return true;
    }
}
