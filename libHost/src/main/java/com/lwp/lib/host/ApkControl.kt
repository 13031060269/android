package com.lwp.lib.host

import android.app.Activity
import android.app.Application
import android.content.*
import android.content.pm.*
import android.content.res.AssetManager
import android.content.res.PluginResource
import android.content.res.Resources
import android.os.Bundle
import com.lwp.lib.host.HostManager.startActivityForResult
import com.lwp.lib.host.classloader.FrameworkClassLoader
import com.lwp.lib.host.classloader.PluginClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

internal class ApkControl(
    hostManager: HostManager,
    val id: String,
    val packageInfo: PackageInfo,
    private val frameworkClassLoader: FrameworkClassLoader
) : PluginClassLoader(
    "${dir(id)}$apkPath",
    "${dir(id)}$optimized",
    "${dir(id)}$libPath", null,
) {
    val mAssetManager: AssetManager = AssetManager::class.java.newInstance()
    internal val mResources: PluginResource
    lateinit var application: Application
    lateinit var ctxWrapper: HostContextWrapper
    val packageName: String = packageInfo.packageName
    val dir = dir(id)
    internal val mApplicationInfo: ApplicationInfo = packageInfo.applicationInfo

    val lifeCycle: ActivityLifeCycle = SimpleLifeCycleCallBack()
    private var launchActivityInfo: ResolveInfo? = null
    private val resolvers = LinkedList<ResolveInfo>()
    private fun activityPath(activityName: String): String = "$dir$activitiesPath$activityName.dex"
    lateinit var inflater: HostLayoutInflater

    override fun createActivityDex(activityName: String): String {
        return ClassGenerator.createActivityDex<Any, Any>(
            activityName,
            id,
            packageName,
            activityPath(activityName)
        )
    }

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
        mApplicationInfo.sourceDir = dir + apkPath
        mApplicationInfo.dataDir = dir
        mApplicationInfo.nativeLibraryDir = dir + libPath
        mApplicationInfo.publicSourceDir = dir + apkPath

        try {
            PackageParser().parseMonolithicPackage(
                File("${dir(id)}$apkPath"), 0
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

    fun push(activityInfo: ActivityInfo) {
        apkId = id
        stack.push(activityInfo)
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

    fun findActivityByClassName(activityName: String): ActivityInfo? {
        packageInfo.activities?.forEach {
            it?.apply {
                if (name == activityName) {
                    applicationInfo = packageInfo.applicationInfo
                    return this
                }
            }
        }
        return null
    }

    fun findActivity(componentName: ComponentName): ActivityInfo? {
        packageInfo.activities?.forEach {
            it?.apply {
                if (packageName == componentName.packageName && name == componentName.className) {
                    applicationInfo = packageInfo.applicationInfo
                    return this
                }
            }
        }
        return null
    }


    fun launch(activity: Activity) {
        launchActivityInfo?.apply {
            launch(activity, Intent().apply {
                component = ComponentName(packageName, activityInfo.name)
            }, -1, null)
        }

    }

    fun launch(activity: Activity, intent: Intent, requestCode: Int, options: Bundle?) {
        activity.startActivityForResult(
            startActivityForResult(activity, id, intent),
            requestCode,
            options
        )
    }

    fun attachBaseContext(romAct: Activity): Array<Any?>? {
        val superclass = romAct::class.java.superclass
        var activityInfo: ActivityInfo? = null
        resolvers.forEach {
            if (it.activityInfo.name == superclass?.name) {
                activityInfo = it.activityInfo
            }
        }
        val actWrapper = ActivityWrapper(ctxWrapper, activityInfo!!)
        return arrayOf(actWrapper, mAssetManager)
    }

    inner class HostContextWrapper(base: Context) :
        ContextWrapper(base) {
        private val mTheme =
            mResources.newTheme().apply { applyStyle(packageInfo.applicationInfo.theme, false) }

        val packageManager =
            HostPM(super.getPackageManager(), this@ApkControl)
        private val fileDir: File = File(dir, "/files/")
        private val cacheDir: File = File(dir, "/caches/")
        private val dataDir: File = File(dir)

        override fun getSystemService(name: String): Any? {
            if (name == LAYOUT_INFLATER_SERVICE) {
                return inflater
            }
            return super.getSystemService(name)
        }

        override fun getCacheDir(): File {
            return cacheDir
        }

        override fun getFilesDir(): File {
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            return fileDir
        }

        override fun getDataDir(): File {
            return dataDir
        }

        override fun getPackageManager(): PackageManager {
            return packageManager
        }


        override fun getApplicationInfo(): ApplicationInfo {
            return mApplicationInfo
        }

        override fun getApplicationContext(): Context {
            return application
        }

        override fun getPackageName(): String {
            return applicationInfo.packageName
        }

        override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
            return super.getSharedPreferences("${packageName}_$name", mode)
        }

        override fun getContentResolver(): ContentResolver {
            return super.getContentResolver()
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

    inner class SimpleLifeCycleCallBack : ActivityLifeCycle {
        var count = 0
        override fun onCreate(pluginAct: Activity) {
            count++
            printLog("${pluginAct::class.java.superclass} onCreate")
        }

        override fun onResume(pluginAct: Activity) {
            printLog("${pluginAct::class.java.superclass} onResume")
        }

        override fun onPause(pluginAct: Activity) {
            printLog("${pluginAct::class.java.superclass} onPause")
        }

        override fun onStart(pluginAct: Activity) {
            printLog("${pluginAct::class.java.superclass} onStart")
        }

        override fun onRestart(pluginAct: Activity) {
            apkId = id
            var activityInfo: ActivityInfo = stack.peek()
            while (stack.isNotEmpty() && activityInfo.name != pluginAct::class.java.superclass.name) {
                stack.pop()
                activityInfo = stack.peek()
            }
            printLog("${pluginAct::class.java.superclass} onRestart")
        }

        override fun onStop(pluginAct: Activity) {
            printLog("${pluginAct::class.java.superclass} onStop")
        }

        override fun onDestroy(pluginAct: Activity) {
            printLog("${pluginAct::class.java.superclass} onDestroy")
            count--
            if (count == 0 && apkId == id) {
                apkId = null
                stack.clear()
            }
        }
    }
}
