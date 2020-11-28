package com.hfax.ucard.utils.mvp.simpleImpl;

import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.base.BaseNetworkFragment;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.MVPUtils;


public class SimpleNetworkAdapter {
    private SimplePresent present;
    final protected Object provider;

    public SimpleNetworkAdapter(BaseNetworkActivity provider) {
        this.provider = provider;
    }

    public SimpleNetworkAdapter(BaseNetworkFragment provider) {
        this.provider = provider;
    }

    public <T> void request(RequestMap map, MVPUtils.Method method, final SimpleViewImpl<T> view) {
        if (present == null) {
            present = new SimplePresent();
        }
        present.request(map, provider, method, view, MVPUtils.getTType(view));
    }

    public <T> void request(String url, MVPUtils.Method method, final SimpleViewImpl<T> view) {
        request(new RequestMap(url), method, view);
    }

    public void request(RequestMap map, MVPUtils.Method method) {
        if (present == null) {
            present = new SimplePresent();
        }
        present.request(map, provider, method, (SimpleView) provider, MVPUtils.getTType(provider));
    }

    public void request(String url, MVPUtils.Method method) {
        request(new RequestMap(url), method);
    }

    public void onDesDroy() {
        if (present != null) {
            present.destroyView();
            present = null;
        }
    }

    public void cancel(String path) {
        if (present != null) {
            present.model.cancel(path);
        }
    }
}
