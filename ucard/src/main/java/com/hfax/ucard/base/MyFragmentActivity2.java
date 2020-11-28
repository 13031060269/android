package com.hfax.ucard.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hfax.ucard.utils.UCardUtil;


/**
 * Created by liuweiping on 2018/5/3.
 */

public class MyFragmentActivity2 extends MyFragmentActivity {


    public static void start(Activity activity, Class<? extends Fragment> fragment) {
        start(activity, fragment, null);
    }

    public static void start(Activity activity, Class<? extends Fragment> fragment, Bundle bundle) {
        Intent intent = new Intent(activity, MyFragmentActivity2.class);
        intent.putExtra(FRAGEMEN, fragment);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        UCardUtil.startActivity(activity, intent);
    }
}
