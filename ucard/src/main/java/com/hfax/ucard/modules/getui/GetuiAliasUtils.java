package com.hfax.ucard.modules.getui;

import android.text.TextUtils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.SPUtils;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.igexin.sdk.PushManager;

/**
 * Created by liuweiping on 2019/2/15.
 */

public class GetuiAliasUtils {
    /**
     * 更新个推别名
     */
    public static void updateGeTuiAlias() {
        String cid = PushManager.getInstance().getClientid(BaseApplication.getContext());
        if (!TextUtils.isEmpty(cid)) {
            String UId = SPUtils.getString(BaseApplication.getContext(), cid, "");
            if (UserModel.isLogin()) {
                if (!TextUtils.equals(UId, LoginBean.getUId()) && PushManager.getInstance().bindAlias(BaseApplication.getContext(), LoginBean.getUId())) {
                    LogUtil.w("getui:设置别名成功！" + LoginBean.getUId() + ";cid=" + cid);
                    SPUtils.putString(BaseApplication.getContext(), cid, LoginBean.getUId());
                }
            } else if (!TextUtils.isEmpty(UId)) {
                if (PushManager.getInstance().unBindAlias(BaseApplication.getContext(), UId, true)) {
                    LogUtil.w("getui:解绑别名成功！" + UId + ";cid=" + cid);
                    SPUtils.remove(BaseApplication.getContext(), cid);
                }
            }
        }

    }
}
