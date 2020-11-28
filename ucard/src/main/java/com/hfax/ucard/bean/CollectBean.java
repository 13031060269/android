package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * Created by Vincent on 2018/5/17.
 */


public class CollectBean implements Serializable {

    CollectResult result;

    boolean success;


    String errorCode;
    String errorMsg;

    public CollectResult getResult() {
        return result;
    }

    public void setResult(CollectResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "CollectBean{" +
                "result=" + result +
                ", success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

}
