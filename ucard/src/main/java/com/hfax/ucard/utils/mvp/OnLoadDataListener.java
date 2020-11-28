package com.hfax.ucard.utils.mvp;

/**
 * Created by eson on 2017/7/21.
 */

public interface OnLoadDataListener<T> {

    void onSuccess(T data);

    void onFail(int code, String msg);
}

