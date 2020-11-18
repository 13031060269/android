package com.lwp.lib.plugin

import android.app.Activity

interface PluginActivityLifeCycle {
    fun onCreate(pluginId: String?, pluginAct: Activity)
    fun onResume(pluginId: String?, pluginAct: Activity)
    fun onPause(pluginId: String?, pluginAct: Activity)
    fun onStart(pluginId: String?, pluginAct: Activity)
    fun onRestart(pluginId: String?, pluginAct: Activity)
    fun onStop(pluginId: String?, pluginAct: Activity)
    fun onDestroy(pluginId: String?, pluginAct: Activity)
}