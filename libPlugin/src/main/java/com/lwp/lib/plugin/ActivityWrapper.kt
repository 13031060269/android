package com.lwp.lib.plugin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.view.ContextThemeWrapper
import java.io.File

internal class ActivityWrapper(base: Context?, appWrapper: Context, plugin: PluginInfo?) :
    ContextThemeWrapper() {
    var plugin: PluginInfo? = null
    private val appWrapper: Context
    override fun getFilesDir(): File {
        return appWrapper.filesDir
    }

    override fun getPackageResourcePath(): String {
        return appWrapper.packageResourcePath
    }

    override fun getPackageCodePath(): String {
        return appWrapper.packageCodePath
    }

    override fun getCacheDir(): File {
        return appWrapper.cacheDir
    }

    override fun getTheme(): Theme {
        return appWrapper.theme
    }

    override fun getBaseContext(): Context {
        return appWrapper
    }

    override fun getPackageManager(): PackageManager {
        return appWrapper.packageManager
    }

    override fun getApplicationInfo(): ApplicationInfo {
        return appWrapper.applicationInfo
    }

    override fun getApplicationContext(): Context {
        return appWrapper.applicationContext
    }

    override fun getPackageName(): String {
        return appWrapper.packageName
    }

    override fun getResources(): Resources {
        return appWrapper.resources
    }

    override fun getAssets(): AssetManager {
        return appWrapper.assets
    }

    init {
        try {
            attachBaseContext(base)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.plugin = plugin
        this.appWrapper = appWrapper
    }
}