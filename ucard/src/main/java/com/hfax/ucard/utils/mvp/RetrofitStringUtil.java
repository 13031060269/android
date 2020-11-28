package com.hfax.ucard.utils.mvp;

import com.hfax.lib.network.RetrofitUtil;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.FragmentLifecycleProvider;
import com.trello.rxlifecycle.kotlin.RxlifecycleKt;

import rx.Observable;
public class RetrofitStringUtil extends RetrofitUtil{
    public static <T> Observable<T> hull(ActivityLifecycleProvider activity,Observable<T> observable) {
        if (activity != null) {
            observable = RxlifecycleKt.bindUntilEvent(observable, activity, ActivityEvent.DESTROY);
        }
        return observable.compose(RetrofitUtil.<T>threadSwitcher());
    }
    public static <T> Observable<T> hull(FragmentLifecycleProvider provider, Observable<T> observable) {
        if (provider != null) {
            observable = RxlifecycleKt.bindUntilEvent(observable, provider, FragmentEvent.DESTROY);
        }
        return observable.compose(RetrofitUtil.<T>threadSwitcher());
    }
    public static <T> Observable<T> hullString(Observable<T> observable) {
        return observable.compose(RetrofitUtil.<T>threadSwitcher());
    }
}
