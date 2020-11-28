package com.hfax.ucard.utils;

import android.text.TextUtils;

import com.hfax.ucard.utils.mvp.DataChange;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.RequestMap;
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent;
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuweiping on 2018/7/16.
 */

public class RepetitionUtils {
    public static Repetition getRepetition() {
        return Repetition.repetition;
    }

    public static class Repetition {
        static Repetition repetition = new Repetition();

        private Repetition() {

        }

        public void submit(final RequestMap map, final Object provider, final DataChange<Boolean> dataChange) {
            RequestMap requestMap = new RequestMap(NetworkAddress.GEN_SUBMIT_TOKEN);
            requestMap.put("num", 1);
            new SimplePresent().request(requestMap, provider, MVPUtils.Method.POST, new SimpleViewImpl<Tokens>() {
                @Override
                public void onSuccess(Tokens ts) {
                    if (ts != null && !UCardUtil.isCollectionEmpty(ts.items)) {
                        Token token = ts.items.get(0);
                        if (token != null && !TextUtils.isEmpty(token.submitToken)) {
                            map.put("submitToken", token.submitToken);
                            dataChange.onChange(true);
                            return;
                        }
                    }
                    UCardUtil.showToast(provider, "请求失败");
                    dataChange.onChange(false);
                }

                @Override
                public void onFail(int code, String msg) {
                    UCardUtil.showToast(provider, msg);
                    dataChange.onChange(false);
                }
            });
        }
    }

    public class Tokens implements Serializable {
        private static final long serialVersionUID = -1l;
        public List<Token> items;
    }

    public class Token implements Serializable {
        private static final long serialVersionUID = -1l;
        public String submitToken;
    }
}
