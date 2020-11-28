package com.hfax.ucard.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.hfax.lib.network.CommonParamInteceptor;
import com.hfax.lib.network.ParamConvertInterceptor;
import com.hfax.lib.network.cookie.OkHttpCookieManager;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.R;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

import static com.hfax.lib.network.RetrofitUtil.DEFAULT_TIME_OUT;

/**
 * Created by eson on 2017/7/12.
 */

public class UCardGlideModule implements GlideModule {
    OkHttpClient client = null;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS).readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS).writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS).cookieJar(new OkHttpCookieManager()).addInterceptor(new CommonParamInteceptor()).addInterceptor(new ParamConvertInterceptor()).hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            client = builder.build();
        }
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}
