package com.hfax.ucard.widget.codes;

import com.hfax.app.BaseActivity;
import com.hfax.ucard.bean.ImageCodeBean;
import com.hfax.ucard.utils.MVPUtils;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

/**
 * 图片验证码管理者
 *
 * @author SongGuangYao
 * @date 2018/7/18
 */

public class ImageCodeManager {

    private static ImageCodeDialog dialog;


    /***
     * 请求图片验证码
     * @param activity
     */
    public static void requestImgCode(final BaseActivity activity, final ImageCodeDialog.CusOnClickListener listener) {
        new SimplePresent().request(new RequestMap(NetworkAddress.IMGCODE_URL), activity, MVPUtils.Method.GET, new SimpleViewImpl<ImageCodeBean>() {
            @Override
            public void onSuccess(ImageCodeBean bean) {
                show(activity, bean, listener);
            }

            @Override
            public void onFail(int code, String msg) {
                activity.showToast(msg);
            }
        });
    }


    /**
     * 显示图片验证码
     *
     * @param activity
     * @param bean
     * @param listener
     */
    private static void show(BaseActivity activity, ImageCodeBean bean, ImageCodeDialog.CusOnClickListener listener) {
        if (dialog == null) {
            dialog = new ImageCodeDialog(activity, bean);
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
            dialog = new ImageCodeDialog(activity, bean);
        }
        dialog.setSureBtnOnclickListener(listener);
        dialog.show();
    }
}
