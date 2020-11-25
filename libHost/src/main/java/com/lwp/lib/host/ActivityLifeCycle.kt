package com.lwp.lib.host

import android.app.Activity

interface ActivityLifeCycle {
    fun onCreate(pluginAct: Activity)
    fun onResume(pluginAct: Activity)
    fun onPause(pluginAct: Activity)
    fun onStart(pluginAct: Activity)
    fun onRestart(pluginAct: Activity)
    fun onStop(pluginAct: Activity)
    fun onDestroy(pluginAct: Activity)
}