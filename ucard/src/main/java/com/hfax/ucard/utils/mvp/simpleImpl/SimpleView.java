package com.hfax.ucard.utils.mvp.simpleImpl;

public interface  SimpleView<T> {
    void onSuccess(T t);
    void onFail(int code, String msg);
}
