package com.hfax.ucard.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.tongdun.android.shell.FMAgent;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * 获取同盾FMId APPList WIFI信息
 *
 * @author SongGuangYao
 * @date 2018/9/10
 */

public class LoanUtils {


    /**
     * 获取FMId
     *
     * @param context
     */
    public static void getLoanInfo(final Context context, final CallBack callBack) {
        final Map<String, Object> finalMap = new HashMap<>();
        Observable.create(new Observable.OnSubscribe<Map<String, Object>>() {
            @Override
            public void call(Subscriber<? super Map<String, Object>> subscriber) {
                String s = FMAgent.onEvent(context.getApplicationContext());
                finalMap.put("wifiInfo", MacUtils.getWifi());
                finalMap.put("appList", MacUtils.getAppList());
                finalMap.put("fingerPrint", s);
                subscriber.onNext(finalMap);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callBack != null) {
                            callBack.error(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Map<String, Object> map) {
                        if (callBack != null) {
                            callBack.callBack(map);
                        }
                    }
                });
    }


    public interface CallBack {
        void callBack(Map<String, Object> map);

        void error(String msg);
    }
}
