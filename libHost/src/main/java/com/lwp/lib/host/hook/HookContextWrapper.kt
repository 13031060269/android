package com.lwp.lib.host.hook

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.view.LayoutInflater
import com.lwp.lib.host.ApkControl
import com.lwp.lib.host.HostInflaterFactory
import com.lwp.lib.host.frameworkClassLoader
import com.lwp.lib.host.hostPackageManager
import java.io.File

internal class HookContextWrapper(
    appWrapper: Context,
    private val control: ApkControl
) :
    ContextWrapper(appWrapper) {
    private val mTheme =
        control.mResources.newTheme()
            .apply { applyStyle(control.packageInfo.applicationInfo.theme, true) }
    private val fileDir: File = File(control.dir, "/files/")
    private val cacheDir: File = File(control.dir, "/caches/")
    private val dataDir: File = File(control.dir)
    private var inflater: LayoutInflater? = null
    override fun getSystemService(name: String): Any? {
        val systemService = super.getSystemService(name)
        if (name == LAYOUT_INFLATER_SERVICE) {
            if (inflater == null) {
                inflater =
                    (systemService as LayoutInflater)
                        .cloneInContext(this).apply { factory = HostInflaterFactory() }
            }
            return inflater
        }
        return systemService
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
        return hostPackageManager
    }

    override fun getApplicationInfo(): ApplicationInfo {
        return control.mApplicationInfo
    }

    override fun getApplicationContext(): Context {
        return control.application
    }

    override fun getPackageName(): String {
        return applicationInfo.packageName
    }

    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
        return super.getSharedPreferences("${packageName}_$name", mode)
    }

    override fun getResources(): Resources = control.mResources

    override fun getAssets(): AssetManager = control.mResources.assets

    override fun getTheme(): Resources.Theme {
        return mTheme
    }

    override fun getClassLoader(): ClassLoader {
        return frameworkClassLoader
    }
}