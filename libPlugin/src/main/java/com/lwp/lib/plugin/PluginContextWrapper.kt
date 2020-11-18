package com.lwp.lib.plugin

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import java.io.File

 internal class PluginContextWrapper(base: Context?, private val plugin: PluginInfo) :
    ContextWrapper(base) {
    private val applicationInfo: ApplicationInfo = ApplicationInfo(super.getApplicationInfo())
    private val fileDir: File
    override fun getFilesDir(): File {
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        return fileDir
    }

    override fun getPackageResourcePath(): String {
        return super.getPackageResourcePath()
    }

    override fun getPackageCodePath(): String {
        return super.getPackageCodePath()
    }

    override fun getCacheDir(): File {
        return super.getCacheDir()
    }

    override fun getApplicationInfo(): ApplicationInfo {
        return applicationInfo
    }

    override fun getApplicationContext(): Context {
        return plugin.application!!
    }

     override fun getPackageManager(): PackageManager {
         return PluginPackageManager(super.getPackageManager())
     }

    override fun getPackageName(): String {
        return plugin.packageName
    }

    override fun getResources(): Resources {
        return plugin.resources!!
    }

    override fun getAssets(): AssetManager {
        return plugin.assetManager!!
    }

    init {
        applicationInfo.sourceDir = plugin.filePath
        applicationInfo.dataDir = ActivityOverrider.getPluginBaseDir(
            plugin.id
        ).absolutePath
        fileDir = File(
            ActivityOverrider.getPluginBaseDir(plugin.id)
                .absolutePath + "/files/"
        )
    }
}