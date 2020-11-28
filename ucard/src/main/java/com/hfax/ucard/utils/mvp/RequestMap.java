package com.hfax.ucard.utils.mvp;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by liuweiping on 2018/4/3.
 */

public class RequestMap extends HashMap<String, Object> {
    private String path;

    public RequestMap(String path) {
        super();
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("请求路径不能为空");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
