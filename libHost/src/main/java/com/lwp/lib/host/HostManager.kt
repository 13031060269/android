package com.lwp.lib.host

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import dalvik.system.DexClassLoader
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

const val lib = "lib"
const val dir = "_dir"

class HostManager private constructor(private val application: Application) {
    private val frameworkClassLoader: FrameworkClassLoader
    private val dexOutputPath: String

    init {
        val optimizedDexPath = application.getDir("plugins", Context.MODE_PRIVATE)
        optimizedDexPath.mkdirs()
        dexOutputPath = optimizedDexPath.absolutePath
        val dexInternalStoragePath = application
            .getDir("plugins", Context.MODE_PRIVATE)
        dexInternalStoragePath.mkdirs()
        val mPackageInfo: Any = HostUtils.getFieldValue(
            application,
            "mBase.mPackageInfo", true
        )
        val classLoader = application.classLoader
        frameworkClassLoader = FrameworkClassLoader(
            classLoader
        )
        HostUtils.setFieldValue(
            mPackageInfo, "mClassLoader",
            frameworkClassLoader, true
        )
    }

    @Throws(Exception::class)
    fun launch(apkFile: File) {
        if (!apkFile.exists() || !apkFile.isFile) return
        application.packageManager
            .getPackageArchiveInfo(
                apkFile.absolutePath,
                PackageManager.GET_ACTIVITIES
//                        or PackageManager.GET_RECEIVERS //
//                        or PackageManager.GET_PROVIDERS //
//                        or PackageManager.GET_META_DATA //
//                        or PackageManager.GET_SHARED_LIBRARY_FILES //
//                        or PackageManager.GET_SERVICES //
                //        or PackageManager.GET_SIGNATURES//
            )?.apply {
                val id = "${packageName}_$versionName"
                val dir = File(dexOutputPath, id + dir)
                val privateFile = File(dir, "base.apk")
                val dexPath = privateFile.absolutePath
                if (!privateFile.exists()) {
                    HostUtils.saveToFile(apkFile, privateFile)
                }
                val libDir = File(dir, lib)
                val zipFile = ZipFile(apkFile, ZipFile.OPEN_READ)
                zipFile.use {
                    if (extractLibFile(it, libDir)) {
                        this.applicationInfo.nativeLibraryDir = libDir.absolutePath
                    }
                }
                val loader = DexClassLoader(
                    dexPath,
                    dexOutputPath,
                    applicationInfo?.nativeLibraryDir,
                    frameworkClassLoader.parent
                )
                val am = AssetManager::class.java.newInstance()
                am.javaClass.getMethod(
                    "addAssetPath", String::
                    class.java
                ).invoke(am, dexPath)
                val baseRes: Resources = application.resources
                val res: Resources = Resources(
                    am, baseRes.displayMetrics,
                    baseRes.configuration
                )
            }
    }

    @Throws(IOException::class)
    private fun extractLibFile(zip: ZipFile, tarDir: File): Boolean {
//        val defaultArch = "arm64-v8a"
        val defaultArch = "x86"
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

    companion object : SingletonHolder<HostManager, Application>(::HostManager)
}
