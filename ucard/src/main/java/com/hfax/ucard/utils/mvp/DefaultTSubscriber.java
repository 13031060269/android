package com.hfax.ucard.utils.mvp;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.hfax.lib.network.BaseResponse;
import com.hfax.lib.utils.LogUtil;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;

import static com.hfax.lib.network.BaseResponse.SERVER_ERR;

/**
 * Created by eson on 16/8/9.
 */
public abstract class DefaultTSubscriber<T> extends rx.Subscriber<T> {
    /**
     * 请求成功
     *
     * @param data 响应数据
     */
    public abstract void onResponse(T data);

    /**
     * 请求失败
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    public abstract void onFailure(int code, String msg);

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {

        if (LogUtil.LOGGABLE) {
            e.printStackTrace();
        }
        String errMsg = null;
        if (e instanceof HttpException) {
            try {
                ResponseBody body = ((HttpException) e).response().errorBody();
                errMsg = body.string();
            } catch (Exception ignored) {
            }
        } else {
            errMsg = e.getMessage();
        }

        boolean isProcess = false;
        if (e instanceof SocketTimeoutException) {
            errMsg = BaseResponse.NETWORK_ERR;
        } else if (e instanceof JsonSyntaxException) {
            if (LogUtil.LOGGABLE) {
                errMsg = "数据解析错误！";
            } else {
                errMsg = SERVER_ERR;
            }
        } else {
            if (!TextUtils.isEmpty(errMsg)) {
                try {
                    LogUtil.e("errMsg:" + errMsg);
                    JSONObject errObj = new JSONObject(errMsg);
                    if (errObj.has("errCode")) {
                        int code = errObj.optInt("errCode");
                        String msg = errObj.optString("errMsg");
                        onFailure(code, msg);
                        isProcess = true;
                    }
                } catch (Exception e1) {
                    LogUtil.w(e1.toString());
                    if (LogUtil.LOGGABLE) {
                        errMsg = "数据解析错误！";
                    } else {
                        errMsg = SERVER_ERR;
                    }
                }
            }
        }
        if (!isProcess) {
            if (TextUtils.isEmpty(errMsg)) {
                errMsg = SERVER_ERR;
            }
            onFailure(BaseResponse.RESPONSE_ERROR, errMsg);

        }
    }

    @Override
    public void onNext(T respond) {
        if (respond != null) {
            onResponse(respond);
        } else {
            if (LogUtil.LOGGABLE) {
                onFailure(BaseResponse.RESPONSE_ERROR, "服务端数据返回为空");
            } else {
                onFailure(BaseResponse.RESPONSE_ERROR, SERVER_ERR);
            }
        }
    }
}
