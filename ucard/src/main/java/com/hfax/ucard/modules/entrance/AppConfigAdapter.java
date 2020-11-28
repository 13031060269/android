package com.hfax.ucard.modules.entrance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.hfax.app.BaseActivity;
import com.hfax.app.h5.H5Activity;
import com.hfax.app.user.view.FingerPrintActivity;
import com.hfax.lib.AppConfig;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.HfaxConstants;
import com.hfax.lib.ui.WebViewFragment;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.base.BaseNetworkActivity;
import com.hfax.ucard.base.MyFragmentActivity2;
import com.hfax.ucard.bean.CacheBean;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.modules.debug.HostSettingActivity;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.user.ForgetLoginPwdActivity;
import com.hfax.ucard.modules.user.LoginActivity;
import com.hfax.ucard.modules.user.LoginByPwdActivity;
import com.hfax.ucard.modules.user.RegisterActivity;
import com.hfax.ucard.utils.FileUtils;
import com.hfax.ucard.utils.GrowingIOUtils;
import com.hfax.ucard.utils.MacUtils;
import com.hfax.ucard.utils.MiitHelper;
import com.hfax.ucard.utils.UCardUtil;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.hfax.ucard.utils.mvp.modle.UserModel;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuweiping on 2018/6/21.
 */

public class AppConfigAdapter extends AppConfig.ConfigAdapter {
    private String deviceName;
    private String memorySize;
    private String storageSize;
    public static String cameraSize;
    private String IMSI;
    private String IMEI;
    private String UA;
    private String versionName;
    private String dpi;

    @Override
    public void logout() {
        UserModel.logout();
    }

    @Override
    public void track(HfaxConstants.GioScene gioScene, String s) {

    }

    @Override
    public void doGIOInject(WebView view) {
//                AppConfigManage.doGIOInject(view);
    }

    @Override
    public void h5InitData(WebViewFragment webViewFragment) {
        SensorsDataAPI.sharedInstance().showUpX5WebView(webViewFragment.getWebView(), true);
    }

    @Override
    public void h5BackPressed(WebViewFragment webViewFragment) {

    }

    @Override
    public void checkHome(final BaseActivity activity) {
//        new SimplePresent().request(new RequestMap(NetworkAddress.H5_DREDGEDEPOSIT_PROTOCOL), this, MVPUtils.Method.GET, new SimpleViewImpl<HomeBean>() {
//            @Override
//            public void onSuccess(HomeBean homeBean) {
//                activity.startActivity(new Intent(activity, MainActivity.class));
//                activity.finish();
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                LogUtil.d("系统还在维护中....");
//            }
//        });
    }

    @Override
    public boolean isLogin() {
        return UserModel.isLogin();
    }

    @Override
    public void inputVerification(Activity activity, int requestCode) {
//                InputVerificationCodeActvity.startActivityForResult(activity, SmsCodeSceneConstant.RECHARGE, requestCode);
    }

    @Override
    public void favorable(Activity activity) {
//                FavorableCommentActivity.show(activity);
    }

    @Override
    public String baseUrl() {
        return NetworkAddress.BASE_URL;
    }

    @Override
    public String getUId() {
        return LoginBean.getUId();
    }

    @Override
    public String getGOID() {
        return LoginBean.getGOID();
    }

    @Override
    public String getMobile() {
        return LoginBean.getMobile();
    }

    @Override
    public void appConfigInfo() {
//                if (AppConfigManage.isPressedHomeKey()) {
//                    AppConfigManage.setIsPressHomeKey(false);
//                    if (!AppConfigManage.isRequestingGio()) {
//                        // 如果当前没有请求则去请求，正在请求时则不再发起请求
//                        AppConfigManage.appConfigInfo();
//                    }
//                }
    }

    @Override
    public void AuthActivityList(List<String> mWithOutAuthClass) {
        mWithOutAuthClass.add(MainActivity.class.getSimpleName());
        mWithOutAuthClass.add(BaseNetworkActivity.class.getSimpleName());
        mWithOutAuthClass.add(GuideActivity.class.getSimpleName());
        mWithOutAuthClass.add(HostSettingActivity.class.getSimpleName());
        mWithOutAuthClass.add(FingerPrintActivity.class.getSimpleName());
        mWithOutAuthClass.add(H5Activity.class.getSimpleName());
        mWithOutAuthClass.add(LoginActivity.class.getSimpleName());
        mWithOutAuthClass.add(LoginByPwdActivity.class.getSimpleName());
        mWithOutAuthClass.add(RegisterActivity.class.getSimpleName());
        mWithOutAuthClass.add(ForgetLoginPwdActivity.class.getSimpleName());
        mWithOutAuthClass.add(MyFragmentActivity2.class.getSimpleName());
    }

    @Override
    public boolean isFromMain(Context context) {
        return context instanceof MainActivity;
    }

    @Override
    public Map<String, String> getCommonParams() {
        Map<String, String> map = new HashMap<>();
        map.put("os", "android");//系统名称
        map.put("application", "122");//区分注册来源
        map.put("os-version", Build.VERSION.RELEASE);//版本号
        map.put("manufacturer", Build.MANUFACTURER);//制造商
        map.put("model", Build.MODEL);//手机型号
        map.put("productName", "hyuke");//app区分
        if (deviceName == null) {
            try {
                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                deviceName = URLEncoder.encode(myDevice.getName(), "utf-8");
            } catch (Exception e) {
            }
        }
        map.put("device-name", deviceName);//	本机名称
        try {
            if (memorySize == null) {
                memorySize = getTotalRam(BaseApplication.getContext());
            }
            map.put("memory-size", memorySize);//	设备内存大小
        } catch (Exception e) {
        }

        try {
            if (storageSize == null) {
                storageSize = getSDTotalSize(BaseApplication.getContext());
            }
            map.put("storage-size", storageSize);//设备存储大小
        } catch (Exception e) {
        }

        map.put("camera", cameraSize);//设备摄像头
        if (versionName == null) {
            versionName = Utils.getVersionName(BaseApplication.getContext());
        }
        map.put("app-version", versionName);//app版本号
        map.put("screen-bright", getSystemBrightness());//屏幕亮度

        try {
            if (IMSI == null) {
                IMSI = getIMSI(BaseApplication.getContext());
            }
            map.put("imsi", IMSI);//imsi
            if (IMEI == null) {
                IMEI = Utils.getIMEI(BaseApplication.getContext());
            }
            map.put("imei", IMEI);//imei
        } catch (Exception e) {
        }
        map.put("mac", MacUtils.getMac(BaseApplication.getContext()));//imei
        if (UA == null) {
            UA = System.getProperty("http.agent");
        }
        map.put("user-agent", UA);//ua
        map.put("ish5", "false");//是否是H5
        if (dpi == null) {
            dpi = Utils.getScreenWidth(BaseApplication.getContext()) + "*" + Utils.getScreenHeight(BaseApplication.getContext());
        }
        map.put("pixel", dpi);//屏幕分辨率
        LoginBean loginBean = CacheBean.getCache(LoginBean.class);
        if (loginBean != null) {
            map.put("userid", loginBean.userid);//imei
            map.put("access-token", loginBean.accessToken);//imei
        }
        map.put("anonymous-id", SensorsDataAPI.sharedInstance().getAnonymousId());
        map.putAll(MiitHelper.getINSTANCE().getData());
        return map;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }

    private String getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(BaseApplication.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(systemBrightness);
    }

    private String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        int ceil = (int) Math.ceil((new Float(Float.valueOf(blockSize * totalBlocks) / 1000 / 1000).doubleValue()));
        return String.valueOf(ceil);
    }

    public static String getTotalRam(Context context) {//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(path);
            br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeCloseable(fileReader);
            FileUtils.closeCloseable(br);
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / 1000).doubleValue()));
        }
        return String.valueOf(totalRam);
    }

    @Override
    public Set<String> getWebsiteWhiteList() {
        Set<String> set = new HashSet<>();
        set.add("*.beneucard.com");
        set.add("*.hfax.com");
        return set;
    }

    @Override
    public void track(String name, JSONObject msg) {
        GrowingIOUtils.trackSDA(name, msg);
    }

    @Override
    public void selectImage(Activity activity, int requestId) {
        UCardUtil.showToast(activity, "暂不支持选择照片，请使用浏览器");
    }
}
