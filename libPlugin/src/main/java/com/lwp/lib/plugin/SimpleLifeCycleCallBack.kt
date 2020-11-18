package com.lwp.lib.plugin

import android.app.Activity

internal object SimpleLifeCycleCallBack : PluginActivityLifeCycle {
    private val counter: HashMap<String?, Int> by lazy {
        HashMap()
    }

    override fun onCreate(pluginId: String?, pluginAct: Activity) {
        counter[pluginId] = (counter[pluginId] ?: 0) + 1
    }

    override fun onResume(pluginId: String?, pluginAct: Activity) {
    }

    override fun onPause(pluginId: String?, pluginAct: Activity) {
    }

    override fun onStart(pluginId: String?, pluginAct: Activity) {
        FrameworkClassLoader.plugId = pluginId
    }

    override fun onRestart(pluginId: String?, pluginAct: Activity) {
    }

    override fun onStop(pluginId: String?, pluginAct: Activity) {
    }

    override fun onDestroy(pluginId: String?, pluginAct: Activity) {
        counter[pluginId] = (counter[pluginId] ?: 0) - 1
//        if (counter[pluginId] == 0) {
//            PluginManager.getInstance().uninstall(pluginId)
//        }
    }
}