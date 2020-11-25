package com.lwp.lib.host

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.*
import android.content.res.AssetManager
import android.content.res.PluginResource
import android.content.res.Resources
import android.os.Bundle
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.HashMap

internal class ApkInfo(
    hostManager: HostManager,
    val id: String,
    val packageInfo: PackageInfo,
    private val frameworkClassLoader: FrameworkClassLoader
) : DexClassLoader(
    "${hostManager.dir(id)}$apkPath",
    "${hostManager.dir(id)}$optimized",
    "${hostManager.dir(id)}$libPath",
    null
) {
    private var stack: Stack<ActivityInfo> = Stack()
    val mAssetManager: AssetManager = AssetManager::class.java.newInstance()
    private val mResources: Resources
    lateinit var application: Application
    lateinit var ctxWrapper: HostContextWrapper
    val packageName: String = packageInfo.packageName
    val dir = hostManager.dir(id)
    private val activityMap = HashMap<String, ActivityLoader>()
    val lifeCycle: ActivityLifeCycle = SimpleLifeCycleCallBack()
    private var launchActivityInfo: ResolveInfo? = null
    private val resolvers = LinkedList<ResolveInfo>()
    fun lib(): String = dir + libPath
    fun activityPath(activityName: String): String = "$dir$activitiesPath$activityName.dex"
    lateinit var inflater: HostLayoutInflater

    init {
        mAssetManager.javaClass.getMethod(
            "addAssetPath", String::
            class.java
        ).invoke(mAssetManager, dir + apkPath)
        val baseRes: Resources = hostManager.application().resources
        mResources = PluginResource(
            mAssetManager, baseRes.displayMetrics,
            baseRes.configuration
        )
        packageInfo.applicationInfo.sourceDir = dir + apkPath
        packageInfo.applicationInfo.dataDir = dir
        packageInfo.applicationInfo.nativeLibraryDir = dir + libPath

        try {
            PackageParser().parseMonolithicPackage(
                File("${hostManager.dir(id)}$apkPath"), 0
            ).apply {
                activities.forEach {
                    val info = it.info
                    if (it.intents.isEmpty()) {
                        resolvers.add(
                            ResolveInfo().apply {
                                activityInfo = info
                            }
                        )
                    } else {
                        it.intents?.forEach {
                            resolvers.add(
                                ResolveInfo().apply {
                                    activityInfo = info
                                    filter = it
                                }
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            packageInfo.activities?.forEach {
                resolvers.add(ResolveInfo().apply {
                    activityInfo = it
                })
            }
        }
        resolvers.forEach {
            if (it.filter != null && it.filter.hasAction("android.intent.action.MAIN")
                && it.filter.hasCategory("android.intent.category.LAUNCHER")
            ) {
                launchActivityInfo = it
            }
        }
        if (launchActivityInfo == null && resolvers.isNotEmpty()) {
            launchActivityInfo = resolvers.first
        }
    }

    suspend fun attach(context: Application) {
        withContext(Dispatchers.Main) {
            val name = packageInfo.applicationInfo?.name
            application = if (name.isNullOrEmpty()) {
                Application()
            } else {
                loadClass(name, true)?.newInstance() as Application
            }
            ctxWrapper = HostContextWrapper(
                context
            )
            inflater = HostLayoutInflater(ctxWrapper)
            val attachMethod =
                Application::class.java.getDeclaredMethod("attach", Context::class.java)
            attachMethod.isAccessible = true
            attachMethod.invoke(application, ctxWrapper)
            Application::class.java.getMethod(
                "registerComponentCallbacks",
                Class.forName("android.content.ComponentCallbacks")
            ).invoke(context, application)
            application.onCreate()
        }
    }

    @kotlin.jvm.Throws(ClassNotFoundException::class)
    public override fun loadClass(className: String, resolve: Boolean): Class<*>? {
        if (className == pluginActivity) {
            val superClass = stack.peek().name
            var actLoader = activityMap[superClass]
            if (actLoader == null) {
                val dexPath = ClassGenerator.createActivityDex<Any, Any>(superClass, this)
                actLoader =
                    ActivityLoader(dexPath, dir + optimized, lib())
                activityMap[superClass] = actLoader
            }
            return actLoader.loadClass(className, resolve)
        }
        var result = findLoadedClass(className)
        if (result == null) {
            try {
                result = findClass(className)
                if (resolve) {
                    resolveClass(result)
                }
            } catch (e: Exception) {
            }
        }
        if (result == null) {
            try {
                result = frameworkClassLoader.loadClass(className, resolve, true)
            } catch (e: Exception) {
            }
        }
        if (result == null) {
            throw ClassNotFoundException(className)
        }
        return result
    }

    fun findActivityByClassName(activityName: String): ActivityInfo? {
        var result: ActivityInfo? = null
        packageInfo.activities?.forEach {
            it?.apply {
                if (name == activityName) {
                    applicationInfo = packageInfo.applicationInfo
                    result = this
                    return@forEach
                }
            }
        }
        return result
    }


    fun startActivityForResult(
        fromAct: Activity?, intent: Intent, requestCode: Int,
        options: Bundle?
    ): Intent {
        packageInfo.activities.forEach {
            it?.apply {
                if (packageName == intent.component?.packageName && intent.component?.className == name) {
                    intent.setClassName(hostPackageName, pluginActivity)
                    stack.push(this)
                    curApk = this@ApkInfo
                }
            }
        }
        return intent
    }

    fun launch(context: Context) {
        curApk = this
        launchActivityInfo?.apply {
            val intent = Intent()
            intent.setClassName(packageName, activityInfo.name)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startActivityForResult(null, intent, 0, null))
        }
    }

    fun attachBaseContext(base: Context?): Array<Any?> {
        val actWrapper = HostWrapper(base, ctxWrapper, this)
        return arrayOf(actWrapper, mAssetManager)
    }

    inner class HostContextWrapper(base: Context) :
        ContextWrapper(base) {
        private val mTheme =
            mResources.newTheme().apply { applyStyle(packageInfo.applicationInfo.theme, false) }
        private val packageManager =
            ApkPackageManager(super.getPackageManager(), this@ApkInfo)
        private val fileDir: File = File(dir, "/files/")

        override fun getSystemService(name: String): Any? {
            if (name == LAYOUT_INFLATER_SERVICE) {
                return inflater
            }
            return super.getSystemService(name)
        }

        override fun getPackageManager(): PackageManager {
            return packageManager
        }

        override fun getFilesDir(): File {
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            return fileDir
        }

        override fun getApplicationInfo(): ApplicationInfo {
            return packageInfo.applicationInfo
        }

        override fun getApplicationContext(): Context {
            return application
        }

        override fun getPackageName(): String {
            return applicationInfo.packageName
        }

        override fun getResources(): Resources = mResources

        override fun getAssets(): AssetManager = mAssetManager

        override fun getTheme(): Resources.Theme {
            return mTheme
        }

        override fun getClassLoader(): ClassLoader {
            return frameworkClassLoader
        }
    }


    inner class ActivityLoader(
        dexPath: String,
        optimizedDirectory: String?,
        librarySearchPath: String?,
    ) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, this) {
        public override fun loadClass(name: String, resolve: Boolean): Class<*>? {
            if (pluginActivity == name) {
                var c = findLoadedClass(name)
                try {
                    if (c == null) {
                        c = findClass(name)
                    }
                    if (resolve) {
                        resolveClass(c)
                    }
                    return c
                } catch (e: Exception) {
                }
            }
            return this@ApkInfo.loadClass(name, resolve)
        }
    }

    inner class SimpleLifeCycleCallBack : ActivityLifeCycle {
        var count = 0
        override fun onCreate(pluginAct: Activity) {
            count++
        }

        override fun onResume(pluginAct: Activity) {
        }

        override fun onPause(pluginAct: Activity) {
        }

        override fun onStart(pluginAct: Activity) {
        }

        override fun onRestart(pluginAct: Activity) {
            curApk = this@ApkInfo
        }

        override fun onStop(pluginAct: Activity) {
        }

        override fun onDestroy(pluginAct: Activity) {
            count--
            if (count == 0 && curApk == this@ApkInfo) {
                curApk = null
            }
        }
    }
}
