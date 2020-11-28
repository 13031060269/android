package com.hfax.ucard.utils.mvp.simpleImpl;

import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.mvp.OnLoadDataListener;
import com.hfax.ucard.utils.mvp.RequestMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SimplePresent {
    SimpleModel model;
    Map<Long, SimpleView> views = new HashMap<>();

    public SimplePresent() {
        model = new SimpleModel();
    }

    /**
     * @param provider 如果是FragmentLifecycleProvider或ActivityLifecycleProvider则请求与生命周期绑定
     * @param method   请求的method
     * @param view
     * @param <T>      回调的返回格式
     */
    public <T> void request(RequestMap map, Object provider, MVPUtils.Method method, final SimpleView<T> view, Type type) {
        final Long key = System.currentTimeMillis();
        views.put(key, view);
        model.doLoadData(map, provider, new OnLoadDataListener<T>() {
            SimpleView<T> getView(Long key) {
                return views.remove(key);
            }

            @Override
            public void onSuccess(T data) {
                SimpleView<T> view = getView(key);
                if (view != null) {
                    view.onSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                SimpleView<T> view = getView(key);
                if (view != null) {
                    view.onFail(code, msg);
                }
            }
        }, method, type);
    }

    public <T> void request(RequestMap map, Object provider, MVPUtils.Method method, final SimpleViewImpl<T> view) {
        request(map, provider, method, view, MVPUtils.getTType(view));
    }

    public void destroyView() {
        views.clear();
    }
}
