package com.hfax.ucard.utils.mvp;//package com.hfax.app.utils;
//

import com.hfax.lib.network.BaseResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by liuweiping on 2018/3/8.
 */

public interface NetworkService {
    /**
     */
    @GET("{path}")
    Observable<Object> getTRequest(@Path(value = "path", encoded = true) String path, @QueryMap RequestMap map);

    /**
     * @return
     */
    @POST("{path}")
    Observable<Object> postTRequest(@Path(value = "path", encoded = true) String path, @Body RequestMap map);

    /**
     * @return
     */
    @FormUrlEncoded
    @POST("{path}")
    Observable<Object> formTRequest(@Path(value = "path", encoded = true) String path, @FieldMap RequestMap map);

    /**
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downLoad(@Url String url);

    /**
     * @return 状态信息
     */
    @POST("{path}")
    Observable<BaseResponse<Object>> uploadFilesWithParts(@Path(value = "path", encoded = true) String path, @Body MultipartBody multipartBody);
}
