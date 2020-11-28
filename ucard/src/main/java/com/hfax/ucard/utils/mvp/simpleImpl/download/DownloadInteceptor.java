package com.hfax.ucard.utils.mvp.simpleImpl.download;

import android.content.SharedPreferences;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.LogUtil;
import com.hfax.ucard.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuweiping on 2018/5/17.
 */

public class DownloadInteceptor implements Interceptor {
    SharedPreferences sharedPreferences = BaseApplication.getContext().getSharedPreferences("DownLoadCookies_Prefs", 0);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        File out=FileUtils.getDownloadFile(url);
        if (out.exists()) {
//            String lastModified = sharedPreferences.getString(url, null);
            Request.Builder builder = request.newBuilder();
//            if (lastModified != null) {
//                builder.addHeader("If-Modified-Since", lastModified);
//            }
//            builder.addHeader("RANGE","bytes="+out.length());
            request = builder.build();
        }
        Response response = chain.proceed(request);
        LogUtil.e("code="+response.code());
        if (response.isSuccessful()) {
//            String value = response.header("Last-Modified");
            FileUtils.loadFile(response.body(),out,false);
//            sharedPreferences.edit().putString(url, value).apply();
        }
        return response;
    }
}
