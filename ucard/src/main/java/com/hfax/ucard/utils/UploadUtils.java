package com.hfax.ucard.utils;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.hfax.lib.network.BaseResponse;
import com.hfax.lib.network.RetrofitUtil;
import com.hfax.ucard.utils.mvp.NetworkService;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * 获取文件 类型
 *
 * @author SongGuangYao
 * @date 2018/6/20
 */

public class UploadUtils {

    /**
     * 获取文件传输类型
     *
     * @param filePath
     * @return 默认图片
     */
    public static String getMimeType(String filePath) {
        if (TextUtils.isEmpty(filePath) || !filePath.contains(".")) {
            return "image/png";
        }
        String suffix = filePath.substring(filePath.lastIndexOf(".")+1, filePath.length());
        Log.e("info---", suffix);
        String typeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);

        return typeFromExtension;
    }

    /**
     * 上传文件
     *
     * @param url        上传地址
     * @param params     其它参数
     * @param fileParams 文件对应参数名列表
     * @param files      文件列表
     * @param <T>
     * @return Observable
     */
    public static synchronized <T> Observable<BaseResponse<Object>> upload(String url, Map<String, String> params, List<String> fileParams, List<File> files) {
        MultipartBody multipartBody = filesToMultipartBody(params, fileParams, files);
        return RetrofitUtil.createService(NetworkService.class)
                .uploadFilesWithParts(url, multipartBody)
                .compose(RetrofitUtil.<BaseResponse<Object>>threadSwitcher());
    }

    /**
     * @param params 其它参数
     * @param names  文件参数 names
     * @param files  文件 files
     * @return 传输body
     */
    public static MultipartBody filesToMultipartBody(Map<String, String> params, List<String> names, List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        File file;
        String param;
        //添加文件
        for (int i = 0; i < names.size(); i++) {
            file = files.get(i);
            param = names.get(i);
            Log.e("info----", file.getName() + "--" + file.getPath());
            MediaType parse = MediaType.parse(getMimeType(file.getName()));
            RequestBody requestBody = RequestBody.create(parse, file);
            builder.addFormDataPart(param, file.getName(), requestBody);
        }

        //添加参数
        if (params != null) {
            Iterator var10 = params.entrySet().iterator();
            while (var10.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) var10.next();
                builder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
            }
        }

        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

}
