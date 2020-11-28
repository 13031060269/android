package com.hfax.ucard.utils.mvp.simpleImpl.download;

import java.io.File;

/**
 * Created by eson on 2017/7/21.
 */

public interface OnDownloadListener {
    void onFail();

    void onSuccess();

    void map(File file) throws  Exception;
}

