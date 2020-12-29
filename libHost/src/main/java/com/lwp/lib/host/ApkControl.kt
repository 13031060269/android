package com.lwp.lib.host

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.*
import android.content.pm.*
import android.content.res.Resources
import android.os.Bundle
import android.view.WindowManager
import com.lwp.lib.host.HostManager.startActivityForResult
import com.lwp.lib.host.classloader.FrameworkClassLoader
import com.lwp.lib.host.classloader.PluginClassLoader
import com.lwp.lib.host.hook.HookContextWrapper
import com.lwp.lib.host.utils.printLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

internal class ApkControl(
    hostManager: HostManager,
    val id: String,
    val packageInfo: PackageInfo,
    frameworkClassLoader: FrameworkClassLoader
) : PluginClassLoader(
    "${dir(id)}$apkPath",
    "${dir(id)}$optimized",
    "${dir(id)}$libPath", frameworkClassLoader.parent,
) {
    internal val mResources: Resources
    lateinit var application: Application
    lateinit var ctxWrapper: HookContextWrapper
    private val packageName: String = packageInfo.packageName
    val dir = dir(id)
    internal val mApplicationInfo: ApplicationInfo = packageInfo.applicationInfo
    var windowAnimations = 0
    val lifeCycle: ActivityLifeCycle = SimpleLifeCycleCallBack()
    private var launchActivityInfo: ResolveInfo? = null
    private val resolvers = LinkedList<ResolveInfo>()
    private fun activityPath(activityName: String): String = "$dir$activitiesPath$activityName.dex"
    private val contextImpl: Context

    override fun createActivityDex(activityName: String): String {
        return ClassGenerator.createActivityDex<Any, Any>(
            activityName,
            id,
            packageName,
            activityPath(activityName)
        )
    }

    init {
        val weak = WeakReference(this)
        idMap[id] = weak
        pkgMap[packageName] = weak
        mApplicationInfo.sourceDir = dir + apkPath
        mApplicationInfo.dataDir = dir
        mApplicationInfo.nativeLibraryDir = dir + libPath
        mApplicationInfo.publicSourceDir = hostManager.application().applicationInfo.sourceDir
        mApplicationInfo.splitPublicSourceDirs =
            arrayOf(dir + apkPath)
        mResources = hostManager.application().packageManager
            .getResourcesForApplication(mApplicationInfo)
        contextImpl = hostManager.application().createPackageContext(packageName, 0)
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

    fun push(fromAct: Activity, activityInfo: ActivityInfo) {
        val layoutParams = fromAct.window.decorView.layoutParams
        if (layoutParams is WindowManager.LayoutParams) {
            windowAnimations = layoutParams.windowAnimations
        }
        apkId = id
        stack.push(activityInfo)
    }

    @SuppressLint("DiscouragedPrivateApi")
    suspend fun attach(context: Application) {
        withContext(Dispatchers.Main) {
            val sysApp = Application::class.java
            application = loadClass(
                packageInfo.applicationInfo?.name ?: sysApp.name,
                true
            ).newInstance() as Application
            ctxWrapper = HookContextWrapper(context, this@ApkControl)
            val attachMethod = sysApp.getDeclaredMethod("attach", Context::class.java)
            attachMethod.isAccessible = true
            attachMethod.invoke(application, ctxWrapper)
            sysApp.getMethod(
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

    fun findActivity(componentName: ComponentName?): ActivityInfo? {
        packageInfo.activities?.forEach {
            it?.apply {
                if (packageName == componentName?.packageName && name == componentName?.className) {
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
        val actWrapper = ctxWrapper
        return arrayOf(actWrapper, mResources.assets)
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
            while (stack.isNotEmpty()
                && activityInfo.name != pluginAct::class.java.superclass?.name
            ) {
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
