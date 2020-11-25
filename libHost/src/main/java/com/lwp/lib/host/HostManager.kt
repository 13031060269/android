package com.lwp.lib.host

import android.app.Application
import android.content.Context
import android.content.pm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.collections.HashMap

const val libPath = "/lib"
private const val dir = "_dir"
const val activitiesPath = "/activities/"
const val apkPath = "/base.apk"
const val optimized = "/optimized"
const val pluginActivity = "com.lwp.lib.host.PluginActivity"
internal val apkMap = HashMap<String, ApkInfo>()
internal var curApk: ApkInfo? = null
internal lateinit var hostPackageName: String
internal lateinit var hostActivityInfo: ActivityInfo

object HostManager {
    private lateinit var frameworkClassLoader: FrameworkClassLoader
    lateinit var outputPath: String
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
        val baseContext = application.baseContext
//        println("2222222222222222222="+baseContext.javaClass)
        hostPackageName = application.packageName
        hostActivityInfo = ActivityInfo().apply {
            name = pluginActivity
            packageName = hostPackageName
        }
        val optimizedDexPath = application.getDir("apkList", Context.MODE_PRIVATE)
        optimizedDexPath.mkdirs()
        outputPath = optimizedDexPath.absolutePath
        val dexInternalStoragePath = application
            .getDir("plugins", Context.MODE_PRIVATE)
        dexInternalStoragePath.mkdirs()
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

    fun application(): Application = application

    fun dir(id: String) = "$outputPath/$id$dir"

    @Throws(Exception::class)
    fun launch(apkFile: File) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (!apkFile.exists() || !apkFile.isFile) return@withContext
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
                    )?.apply {
//                        val id = packageName
                        val id = "${packageName}_${versionName}_$versionCode"
                        if (apkMap[id] == null) {
                            val dir = File(outputPath, id + dir)
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
                            val apkInfo =
                                ApkInfo(this@HostManager, id, this, frameworkClassLoader)
                            apkInfo.attach(application)
                            apkMap[id] = apkInfo
                        }
                        apkMap[id]?.launch(application)
                    }
            }
        }
    }

    @Throws(IOException::class)
    private fun extractLibFile(zip: ZipFile, tarDir: File): Boolean {
        val defaultArch = "armeabi-v7a"
//        val defaultArch = "x86"
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
                    defaultArch
                }
                var ents = archLibEntries[en2add]
                if (ents == null) {
                    ents = LinkedList()
                    archLibEntries[en2add] = ents
                }
                ents.add(entry)
            }
        }
        val arch = System.getProperty("os.arch")
        var libEntries: List<ZipEntry>? = archLibEntries[arch!!.toLowerCase(Locale.getDefault())]
        if (libEntries == null) {
            libEntries = archLibEntries[defaultArch]
        }
        var hasLib = false
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
        return hasLib
    }

//    companion object : SingletonHolder<HostManager, Application>(::HostManager)
}
