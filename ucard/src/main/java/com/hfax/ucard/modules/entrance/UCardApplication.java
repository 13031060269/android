package com.hfax.ucard.modules.entrance;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bun.miitmdid.core.JLibrary;
import com.hfax.app.utils.SingleActivityUtil;
import com.hfax.lib.AppConfig;
import com.hfax.lib.BaseApplication;
import com.hfax.lib.network.RetrofitUtil;
import com.hfax.lib.utils.LogUtil;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.BuildConfig;
import com.hfax.ucard.R;
import com.hfax.ucard.modules.debug.DebugUtil;
import com.hfax.ucard.modules.home.MainActivity;
import com.hfax.ucard.modules.user.LoginActivity;
import com.hfax.ucard.utils.*;
import com.hfax.ucard.utils.mvp.NetworkAddress;
import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;
import com.qipeng.capatcha.QPCapatcha;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.ui.UILifecycleListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UCardApplication extends BaseApplication {

    private static final String WEIXIN_API_KEY = "wxcdc244ef62d6d91d";
    final Handler mHandler = new Handler();
    static UCardApplication INSTANCE;
    ActivityLifecycleCallbacksUtils activityLifecycleCallbacksUtils = new ActivityLifecycleCallbacksUtils();
    private static Typeface sAlternateBoldTypeface;

    @Override
    public void create() {
        Utils.CHANNEL = getChannel();
        INSTANCE = this;
        LogUtil.LOGGABLE = BuildConfig.DEBUG;
        RetrofitUtil.BASE_PATH = "";
        if (!DebugUtil.init(this)) {
            RetrofitUtil.init(NetworkAddress.BASE_URL);
        }
        /**设置单例Activity*/
        setSingleActivities();

        QPCapatcha.getInstance().init(this, "df825fad64ab4f90a299f7f1fab4cc8d");

        initSensorsDataSDK(this);
        /*** bugly设置*/
        initBugly();

        /***Growing IO 设置*/
//        initGIO();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacksUtils);
        // 加载字体
        sAlternateBoldTypeface = Typeface.createFromAsset(getAssets(), "dinmittelschrift.ttf");
        initLeakCanary();
    }


    void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

    /**
     * 初始化 SDK 、设置公共属性、开启自动采集
     */
    private void initSensorsDataSDK(Context context) {
        try {
            // 初始化 SDK
            SAConfigOptions saConfigOptions = new SAConfigOptions(BuildConfig.DEBUG ? NetworkAddress.URL_SDA_DEBUG : NetworkAddress.URL_SDA);
            saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                    SensorsAnalyticsAutoTrackEventType.APP_START |
                    SensorsAnalyticsAutoTrackEventType.APP_END |
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN);
            SensorsDataAPI.sharedInstance(context, saConfigOptions);                                                                          // 传入 Context
//                    BuildConfig.DEBUG ? SensorsDataAPI.DebugMode.DEBUG_AND_TRACK : SensorsDataAPI.DebugMode.DEBUG_OFF); // Debug 模式选项
            GrowingIOUtils.setUserId();
            // 初始化SDK后，获取应用名称设置为公共属性
            JSONObject properties = new JSONObject();
            properties.put("app_name", "惠域U卡");
            properties.put("platform_type", "Android");
            if (BuildConfig.DEBUG) {
                properties.put("location_host", NetworkAddress.BASE_URL);
            }
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);

//
//            // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
//            List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
//            // $AppStart
//            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_START);
//            // $AppEnd
//            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_END);
//            // $AppViewScreen
//            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
//            // $AppClick
//            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
//            SensorsDataAPI.sharedInstance().enableAutoTrack(eventTypeList);
            SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
            SensorsDataAPI.sharedInstance().enableReactNativeAutoTrack();
            SensorsDataAPI.sharedInstance().trackAppCrash();
//            SensorsDataAPI.sharedInstance().enableHeatMap();
            SensorsDataAPI.sharedInstance().enableLog(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getChannel() {
        String channel = null;
        try {
            ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(getApplicationContext());
            if (channelInfo != null) {
                channel = WalleChannelReader.getChannelInfo(getApplicationContext()).getChannel();
            }
        } catch (Exception e) {
        }
        return UCardUtil.isEmpty(channel) ? "3000000225" : channel;
    }

    public static Typeface getAlternateBoldTtf() {
        return sAlternateBoldTypeface;
    }

//    private void initGIO() {
//        GrowingIO.startWithConfiguration(this, new Configuration()
//                .useID()
//                .setChannel(Utils.CHANNEL)//渠道号
//                .setDebugMode(BuildConfig.DEBUG)
//                .trackAllFragments());
//        GrowingIOUtils.setUserId();
//    }

    /**
     * @return 是否运行在后台
     */
    public boolean isRunBackground() {
        return activityLifecycleCallbacksUtils.mFinalCount == 0;
    }

    private void setSingleActivities() {
        ArrayList<String> activities = new ArrayList<>();
        activities.add(LoginActivity.class.getName());
        SingleActivityUtil.getInstance().setSingleActivitys(activities);
    }

    @Override
    protected void initAppConfig() {
        AppConfig.adapter = new AppConfigAdapter();
        AppConfig.LOGIN_AVTIVITY = LoginActivity.class;
        AppConfig.WEIXIN_API_KEY = WEIXIN_API_KEY;
        AppConfig.IMUtil = new IMUtils();
    }

    public static UCardApplication getInstance() {
        return INSTANCE;
    }

    public void runOnUIThread(Runnable run) {
        runOnUIThread(run, 0);
    }

    public void runOnUIThread(Runnable run, long delayMillis) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
        } else {
            mHandler.postDelayed(run, delayMillis);
        }
    }

    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel(Utils.CHANNEL);
        strategy.setAppVersion(Utils.APP_VERSION + "(" + Utils.getVersionCode(this) + ")");
        strategy.setAppPackageName(Utils.getAppPackageName(this));
        strategy.setCrashHandleCallback(new CrashHandleCallback() {
            @Override
            public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(getApplicationContext());
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }

            @Override
            public synchronized byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });
        Beta.upgradeDialogLayoutId = R.layout.manual_upgrade_layout;
        Beta.canShowUpgradeActs.add(MainActivity.class);
        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Beta.strUpgradeDialogCancelBtn = "";
        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
            @Override
            public void onCreate(final Context context, View view, final UpgradeInfo upgradeInfo) {
            }

            @Override
            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {
            }

            @Override
            public void onResume(Context context, final View view, UpgradeInfo upgradeInfo) {
                //大版本升级时的版本号
                TextView mBigVersionCode = (TextView) view.findViewById(R.id.tv_upgrade_version);
                //大版本页面
                RelativeLayout mBigVersion = (RelativeLayout) view.findViewById(R.id.rl_big_upgrade);
                //小版本升级页面
                LinearLayout mSmallVersion = (LinearLayout) view.findViewById(R.id.ll_small_upgrade);
                //小版本升级时的版本号
                TextView mSmallVersionCode = (TextView) view.findViewById(R.id.tv_upgrade_title);
                final View cancle = view.findViewById(R.id.iv_upgrade_cancel_image);
                final View cancleSmall = view.findViewById(R.id.iv_cancel_2);
                cancleSmall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancle.performClick();
                    }
                });
                if (upgradeInfo != null) {
                    //大版本更新
                    if (VersionUtils.isBigVersion(upgradeInfo.versionName)) {
                        mBigVersionCode.setText("V " + upgradeInfo.versionName);
                        mBigVersion.setVisibility(View.VISIBLE);
                        mSmallVersion.setVisibility(View.GONE);
                        cancle.setVisibility(View.GONE);
                    } else {
                        //小版本更新
                        mBigVersion.setVisibility(View.GONE);
                        mSmallVersion.setVisibility(View.VISIBLE);
                        mSmallVersionCode.setText("V " + upgradeInfo.versionName);
                        cancleSmall.setVisibility(View.GONE);
                    }
                    /**
                     * 如果是强制更新则隐藏取消图片
                     * upgradeInfo.upgradeType
                     * 1：推荐升级
                     * 2：强制升级
                     */
                    if (2 == upgradeInfo.upgradeType) {
                        cancle.setVisibility(View.GONE);
                        cancleSmall.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
            }

            @Override
            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
            }

            @Override
            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
            }
        };
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = UCardUtil.getProcessName();
        // 设置是否为上报进程
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        Bugly.init(getApplicationContext(), Utils.getMetaData(getApplicationContext(), "BUGLY_APP_ID"), BuildConfig.DEBUG, strategy);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        try {
            JLibrary.InitEntry(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
