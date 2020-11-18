package com.lwp.lib.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lwp.lib.plugin.PluginUtils.fileMd5;

public class PluginManager {
    private static final PluginManager instance = new PluginManager();

    private final Map<String, PluginInfo> ids = new HashMap<>();
    private final Map<String, PluginInfo> packages = new ConcurrentHashMap<>();
    public WeakReference<Application> context;
    private String dexOutputPath;
    public volatile boolean hasInit = false;
    private File dexInternalStoragePath;
    private FrameworkClassLoader frameworkClassLoader;
    private PluginActivityLifeCycle pluginActivityLifeCycle;

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return instance;
    }

    public static void init(Application application) {
        instance.initByApplication(application);
    }

    private void initByApplication(Application application) {
        if (hasInit) return;
        context = new WeakReference<>(application);
        File optimizedDexPath = application.getDir("plugs_out", Context.MODE_PRIVATE);
        optimizedDexPath.mkdirs();
        dexOutputPath = optimizedDexPath.getAbsolutePath();
        dexInternalStoragePath = application
                .getDir("plugins", Context.MODE_PRIVATE);
        dexInternalStoragePath.mkdirs();
        try {
            Object mPackageInfo = PluginUtils.getFieldValue(application,
                    "mBase.mPackageInfo", true);
            ClassLoader classLoader = application.getClassLoader();
            frameworkClassLoader = new FrameworkClassLoader(
                    classLoader);
            PluginUtils.setFieldValue(mPackageInfo, "mClassLoader",
                    frameworkClassLoader, true);
            createPlugInfo(new File(application.getApplicationInfo().sourceDir), null, classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hasInit = true;

    }

    private void checkInit() {
        if (!hasInit) {
            throw new IllegalStateException("请先初始化！");
        }
    }

    public PluginInfo getPluginById(String pluginId) {
        return ids.get(pluginId);
    }

    public PluginInfo getPluginByPackageName(String packageName) {
        return packages.get(packageName);
    }

    public Collection<PluginInfo> getPlugins() {
        return ids.values();
    }

    public void uninstall(String pluginId) {
        checkInit();
        PluginInfo pl;
        synchronized (this) {
            pl = ids.remove(pluginId);
            if (pl != null) {
                packages.remove(pl.getPackageName());
            }
        }
        if (pl != null && context.get() != null) {
            try {
                Application.class.getMethod(
                        "unregisterComponentCallbacks",
                        Class.forName("android.content.ComponentCallbacks"))
                        .invoke(context.get(), pl.getApplication());
            } catch (Exception ignored) {
            }
        }
        System.gc();
    }

    public boolean launch(String apkPath, Context context) throws Exception {
        checkInit();
        File file = new File(apkPath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        PluginInfo pluginInfo = createPlugInfo(file);
        return pluginInfo.launch(context);
    }

    private PluginInfo createPlugInfo(File pluginApk) throws Exception {
        return createPlugInfo(pluginApk, pluginApk.getAbsolutePath().replace("/", "_"), null);
    }

    private PluginInfo createPlugInfo(File pluginApk, String id, ClassLoader loader) throws Exception {
        PluginInfo info = ids.get(id);
        if (info != null) {
            return info;
        }
        info = new PluginInfo();
        info.setId(id);
        File privateFile;
        if (id == null) {
            privateFile = pluginApk;
        } else {
            privateFile = new File(dexInternalStoragePath, id);
        }


        info.setFilePath(privateFile.getAbsolutePath());
        String dexPath = privateFile.getAbsolutePath();
        if (fileMd5(pluginApk) != fileMd5(privateFile)) {
            PluginUtils.saveToFile(pluginApk, privateFile);
        }
        PluginManifestUtil.setManifestInfo(context.get(), dexPath, info);
        if (loader == null) {
            loader = new PluginClassLoader(dexPath,
                    dexOutputPath, frameworkClassLoader.getParent(), info);
        }
        info.setClassLoader(loader);

        try {
            AssetManager am = AssetManager.class.newInstance();
            am.getClass().getMethod("addAssetPath", String.class)
                    .invoke(am, dexPath);
            info.setAssetManager(am);
            Resources baseRes = context.get().getResources();
            Resources res = new Resources(am, baseRes.getDisplayMetrics(),
                    baseRes.getConfiguration()) {
            };
            info.setResources(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        packages.put(info.getPackageName(), info);
        ids.put(info.getId(), info);
        return info;
    }

    void initPluginApplication(final PluginInfo info, Activity actFrom)
            throws Exception {
        initPluginApplication(info, actFrom, false);
    }

    private void initPluginApplication(final PluginInfo plugin, Activity actFrom, boolean onLoad) throws Exception {
        if (!onLoad && plugin.getApplication() != null) {
            return;
        }
        final String className = plugin.getPackageInfo().applicationInfo.name;
        if (className == null) {
            if (onLoad) {
                return;
            }
            Application application = new Application();
            setApplicationBase(plugin, application);
            return;
        }

        Runnable setApplicationTask = new Runnable() {
            public void run() {
                ClassLoader loader = plugin.getClassLoader();
                try {
                    Class<?> applicationClass = loader.loadClass(className);
                    Application application = (Application) applicationClass
                            .newInstance();
                    setApplicationBase(plugin, application);
                    application.onCreate();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        if (actFrom == null) {
            if (onLoad)
                return;
            setApplicationTask.run();
        } else {
            actFrom.runOnUiThread(setApplicationTask);
        }
    }

    private synchronized void setApplicationBase(PluginInfo plugin, Application application)
            throws Exception {

        if (plugin.getApplication() != null) {
            return;
        }
        plugin.setApplication(application);
        //
        PluginContextWrapper ctxWrapper = new PluginContextWrapper(context.get(),
                plugin);
        plugin.appWrapper = ctxWrapper;
        Method attachMethod = Application.class.getDeclaredMethod("attach", Context.class);
        attachMethod.setAccessible(true);
        attachMethod.invoke(application, ctxWrapper);
        if (context.get() != null) {
            Application.class.getMethod("registerComponentCallbacks",
                    Class.forName("android.content.ComponentCallbacks"))
                    .invoke(context.get(), application);
        }
    }

    File getDexInternalStoragePath() {
        return dexInternalStoragePath;
    }

    public Context context() {
        return context.get();
    }

    public PluginActivityLifeCycle getPluginActivityLifeCycle() {
        if (pluginActivityLifeCycle == null) {
            pluginActivityLifeCycle = SimpleLifeCycleCallBack.INSTANCE;
        }
        return pluginActivityLifeCycle;
    }

    FrameworkClassLoader getFrameworkClassLoader() {
        return frameworkClassLoader;
    }
}
