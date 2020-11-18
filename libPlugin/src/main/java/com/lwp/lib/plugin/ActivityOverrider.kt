package com.lwp.lib.plugin

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.lwp.lib.plugin.ClassGenerator.createActivityDex
import java.io.File
import java.lang.reflect.Field

internal class ActivityOverrider {
    companion object {
        val targetClassName = PluginActivity::class.java.name

        @JvmStatic
        fun overrideStartActivityForResult(
            fromAct: Activity?, pluginId: String?, intent: Intent, requestCode: Int,
            options: Bundle?
        ): Intent {
            val plugin = PluginManager.getInstance()
            if (intent.component != null) {
                val component = intent.component
                val pkg = component!!.packageName
                val toActName = component.className
                val thisPlugin = plugin.getPluginById(pluginId)
                var actInThisApk: ActivityInfo? = null
                var plug = thisPlugin
                if (pkg == thisPlugin.packageName) {
                    actInThisApk = thisPlugin
                        .findActivityByClassName(toActName)
                } else {
                    val otherPlug = plugin.getPluginByPackageName(pkg)
                    if (otherPlug != null) {
                        plug = otherPlug
                        actInThisApk = otherPlug
                            .findActivityByClassName(toActName)
                    }
                }
                if (actInThisApk != null) {
                    setPluginIntent(intent, plug, actInThisApk.name)
                } else {
                    for (pluginInfo in plugin.plugins) {
                        if (pluginInfo === thisPlugin) {
                            continue
                        }
                        val otherAct = pluginInfo
                            .findActivityByClassName(toActName)
                        if (otherAct != null) {
                            setPluginIntent(intent, pluginInfo, otherAct.name)
                            break
                        }
                    }
                }
            } else if (intent.action != null) {
                val action = intent.action
                val thisPlugin = plugin.getPluginById(pluginId)
                val actInThisApk = thisPlugin.findActivityByAction(action)
                if (actInThisApk != null) {
                    setPluginIntent(intent, thisPlugin, actInThisApk.name)
                } else {
                    for (pluginInfo in plugin.plugins) {
                        if (pluginInfo === thisPlugin) {
                            continue
                        }
                        val otherAct = pluginInfo
                            .findActivityByAction(action)
                        if (otherAct != null) {
                            setPluginIntent(intent, pluginInfo, otherAct.name)
                            break
                        }
                    }
                }
            }
            return intent
        }

        private fun setPluginIntent(
            intent: Intent, plugin: PluginInfo,
            actName: String
        ) {
            val apkPlugin = PluginManager.getInstance()
            createProxyDex(plugin, actName)
            val act = apkPlugin.frameworkClassLoader.newActivityClassName(
                plugin.id, actName
            )
            intent.component = ComponentName(apkPlugin.context(), act)
        }

        fun getPluginBaseDir(pluginId: String?): File {
            val pluginPath = PluginManager.getInstance()
                .dexInternalStoragePath.absolutePath
            val pluginDir = "$pluginPath/$pluginId-dir/"
            val folder = File(pluginDir)
            folder.mkdirs()
            return folder
        }
        @JvmStatic
        fun getPluginLibDir(pluginId: String?): File {
            return File(getPluginBaseDir(pluginId).toString() + "/lib/")
        }

        fun getPorxyActivityDexPath(pluginId: String?, activity: String): File {
            val folder = File(getPluginBaseDir(pluginId).toString() + "/activities/")
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val suffix = ".dex"
            return File(folder, activity + suffix)
        }

        @JvmOverloads
        fun createProxyDex(plugin: PluginInfo, activity: String, lazy: Boolean = true): File {
            val savePath = getPorxyActivityDexPath(plugin.id, activity)
            createProxyDex(plugin, activity, savePath, lazy)
            return savePath
        }

        private fun createProxyDex(
            plugin: PluginInfo, activity: String, saveDir: File,
            lazy: Boolean
        ) {
            if (lazy && saveDir.exists()) {
                return
            }
            try {
                val pkgName = plugin.packageName
                createActivityDex(activity, targetClassName, saveDir, plugin.id!!, pkgName)
            } catch (ignored: Throwable) {
            }
        }

        @JvmStatic
        fun overrideAttachBaseContext(
            pluginId: String?,
            fromAct: Activity?,
            base: Context?
        ): Array<Any?>? {

            //
            val plugin = PluginManager.getInstance().getPluginById(pluginId) ?: return null
            if (plugin.application == null) {
                try {
                    PluginManager.getInstance().initPluginApplication(
                        plugin,
                        null
                    )
                } catch (ignored: Exception) {
                }
            }
            val actWrapper = ActivityWrapper(base, plugin.appWrapper, plugin)
            return arrayOf(actWrapper, plugin.assetManager)
        }

        @JvmStatic
        private fun changeActivityInfo(activity: Context) {
            val actName = activity.javaClass.superclass!!.name
            if (activity.javaClass.name != targetClassName) {
                return
            }
            var field_mActivityInfo: Field? = null
            try {
                field_mActivityInfo = Activity::class.java.getDeclaredField("mActivityInfo")
                field_mActivityInfo.isAccessible = true
            } catch (e: Exception) {
                return
            }
            val con = PluginManager.getInstance()
            val plugin = con.getPluginByPackageName(activity.packageName)
            val actInfo = plugin.findActivityByClassName(actName)
            actInfo!!.applicationInfo = plugin.packageInfo.applicationInfo
            try {
                field_mActivityInfo[activity] = actInfo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun getPlugActivityTheme(fromAct: Activity, pluginId: String?): Int {
            val con = PluginManager.getInstance()
            val plugin = con.getPluginById(pluginId)
            val actName = fromAct.javaClass.superclass!!.name
            val actInfo = plugin.findActivityByClassName(actName)
            val rs = actInfo!!.themeResource
            changeActivityInfo(fromAct)
            return rs
        }

        @JvmStatic
        fun overrideOnBackPressed(fromAct: Activity, pluginId: String?): Boolean {
            val plInfo = PluginManager.getInstance().getPluginById(pluginId)
            val actName = fromAct.javaClass.superclass!!.name
            val actInfo = plInfo.findActivityByClassName(actName)
            val finish = plInfo.isFinishActivityOnBackPressed(actInfo)
            if (finish) {
                fromAct.finish()
            }
            return plInfo.isInvokeSuperOnBackPressed(actInfo)
        }

        @JvmStatic
        fun callback_onCreate(pluginId: String?, fromAct: Activity) {
            val con = PluginManager.getInstance()
            val plugin = con.getPluginById(pluginId)
            try {
                val applicationField = Activity::class.java
                    .getDeclaredField("mApplication")
                applicationField.isAccessible = true
                applicationField[fromAct] = plugin.application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            run {
                val actName = fromAct.javaClass.superclass!!.name
                val actInfo = plugin.findActivityByClassName(actName)
                val resTheme = actInfo!!.themeResource
                if (resTheme != 0) {
                    var hasNotSetTheme = true
                    try {
                        val mTheme = ContextThemeWrapper::class.java
                            .getDeclaredField("mTheme")
                        mTheme.isAccessible = true
                        hasNotSetTheme = mTheme[fromAct] == null
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (hasNotSetTheme) {
                        changeActivityInfo(fromAct)
                        fromAct.setTheme(resTheme)
                    }
                }
            }
            val callback = con
                .pluginActivityLifeCycle
            try {
                callback?.onCreate(pluginId!!, fromAct)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun callback_onResume(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onResume(pluginId!!, fromAct!!)
        }

        @JvmStatic
        fun callback_onStart(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onStart(pluginId!!, fromAct!!)
        }

        @JvmStatic
        fun callback_onRestart(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onRestart(pluginId!!, fromAct!!)
        }

        @JvmStatic
        fun callback_onPause(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onPause(pluginId!!, fromAct!!)
        }

        @JvmStatic
        fun callback_onStop(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onStop(pluginId!!, fromAct!!)
        }

        @JvmStatic
        fun callback_onDestroy(pluginId: String?, fromAct: Activity?) {
            val callback = PluginManager.getInstance()
                .pluginActivityLifeCycle
            callback?.onDestroy(pluginId!!, fromAct!!)
        }
    }
}