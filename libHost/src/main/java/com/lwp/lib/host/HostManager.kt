package com.lwp.lib.host

import android.app.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import android.content.res.Resources
import com.lwp.lib.host.classloader.FrameworkClassLoader
import com.lwp.lib.host.hook.HookPM
import com.lwp.lib.host.utils.HostUtils
import com.lwp.lib.host.utils.hook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.collections.HashMap

internal val idMap = HashMap<String?, WeakReference<ApkControl>>()
internal val pkgMap = HashMap<String?, WeakReference<ApkControl>>()

@Volatile
internal var apkId: String? = null
internal lateinit var hostActivityInfo: ActivityInfo
internal lateinit var hostComponentName: ComponentName
internal lateinit var frameworkClassLoader: FrameworkClassLoader
internal lateinit var resources: Resources
internal lateinit var hostPackageManager: PackageManager
internal fun findApkInfo(tag: String?): ApkControl? {
    return idMap[tag]?.get() ?: pkgMap[tag]?.get()
}

object HostManager {

    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
        initFirst(application)
        hook()
        hostPackageManager = HookPM(application.packageManager)
        resources = application.resources
        hostComponentName = ComponentName(application.packageName, pluginActivity)
        hostActivityInfo = ActivityInfo()
        hostActivityInfo.packageName = hostComponentName.packageName
        hostActivityInfo.name = pluginActivity
        val mPackageInfo: Any = HostUtils.getFieldValue(
            application,
            "mBase.mPackageInfo", true
        )
        val classLoader = application.classLoader
        frameworkClassLoader = FrameworkClassLoader(classLoader)
        HostUtils.setFieldValue(
            mPackageInfo, "mClassLoader",
            frameworkClassLoader, true
        )
    }

    fun startActivityForResult(fromAct: Activity, id: String, intent: Intent): Intent {
        return intent.apply {
            component?.apply {
                findActivityInfo(this)?.apply {
                    component = hostComponentName
                    findApkInfo(packageName)!!.push(fromAct, this)
                }
            }
        }
    }

    private fun findActivityInfoByName(name: String): ActivityInfo? {
        idMap.values.forEach {
            it.get()?.findActivityByClassName(name)?.apply {
                return this
            }
        }
        return null
    }

    internal fun findActivityInfo(componentName: ComponentName): ActivityInfo? {
        return findActivityInfo(componentName.packageName, componentName) ?: findActivityInfoByName(
            componentName.className
        )
    }

    private fun findActivityInfo(id: String, componentName: ComponentName): ActivityInfo? {
        return findApkInfo(id)?.findActivity(componentName)
    }


    fun application(): Application = application

    @Throws(Exception::class)
    suspend fun install(path: String): String {
        return withContext(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val apkFile = File(path)
                application.packageManager
                    .getPackageArchiveInfo(
                        apkFile.absolutePath,
                        PackageManager.GET_ACTIVITIES
                                or PackageManager.GET_RECEIVERS //
                                or PackageManager.GET_PROVIDERS //
                                or PackageManager.GET_META_DATA //
                                or PackageManager.GET_SHARED_LIBRARY_FILES //
                                or PackageManager.GET_SERVICES //
                                or PackageManager.GET_SIGNATURES //
                    )?.run {
                        val id = "${packageName}_${versionName}_$versionCode"
                        if (idMap[id]?.get() == null) {
                            val dir = File(dir(id))
                            if (DEBUG) {
                                clear(id, packageName)
                            }
                            val libDir = File(dir, libPath)
                            dir.mkdirs()
                            File(dir, libPath).mkdirs()
                            File(dir, activitiesPath).mkdirs()
                            File(dir, optimized).mkdirs()
                            libDir.mkdirs()
                            val privateFile = File(dir, apkPath)
                            if (!privateFile.exists()) {
                                HostUtils.saveToFile(apkFile, privateFile)
                            }
                            try {
                                ZipFile(apkFile, ZipFile.OPEN_READ).use {
                                    extractLibFile(it, libDir)
                                }
                            } catch (e: Exception) {
                            }

                            ApkControl(this@HostManager, id, this, frameworkClassLoader).attach(
                                application
                            )
                        }
                        id
                    } ?: run {
                    throw RuntimeException("创建插件失败！！！$path")
                }
            }
        }
    }

    fun launch(pkg: String, activity: Activity) {
        findApkInfo(pkg)!!.launch(activity)
    }

    @Throws(IOException::class)
    private fun extractLibFile(zip: ZipFile, tarDir: File): Boolean {
        val archLibEntries: MutableMap<String, MutableList<ZipEntry>> = HashMap()
        val e = zip.entries()
        while (e
                .hasMoreElements()
        ) {
            val entry = e.nextElement()
            var name = entry.name
            if (name.startsWith("/")) {
                name = name.substring(1)
            }
            if (name.startsWith("lib/")) {
                if (entry.isDirectory) {
                    continue
                }
                val sp = name.indexOf('/', 4)
                var en2add: String
                en2add = if (sp > 0) {
                    val osArch = name.substring(4, sp)
                    osArch.toLowerCase(Locale.getDefault())
                } else {
                    continue
                }
                var ents = archLibEntries[en2add]
                if (ents == null) {
                    ents = LinkedList()
                    archLibEntries[en2add] = ents
                }
                ents.add(entry)
            }
        }
        var hasLib = false
        android.os.Build.SUPPORTED_ABIS.forEach {
            val libEntries = archLibEntries[it?.toLowerCase(Locale.getDefault())]
            if (libEntries != null) {
                hasLib = true
                tarDir.mkdirs()
                for (libEntry in libEntries) {
                    val name = libEntry.name
                    val pureName = name.substring(name.lastIndexOf('/') + 1)
                    val target = File(tarDir, pureName)
                    HostUtils.saveToFile(zip.getInputStream(libEntry), target)
                }
            }
        }
        return hasLib
    }

//    companion object : SingletonHolder<HostManager, Application>(::HostManager)
}
