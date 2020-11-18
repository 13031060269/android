package com.lwp.lib.pluginhostpublic

interface ActivityLifeCycle {
    fun onCreate(id: String, activity: String)
    fun onRestart(id: String, activity: String)
    fun onStart(id: String, activity: String)
    fun onResume(id: String, activity: String)
    fun onPause(id: String, activity: String)
    fun onStop(id: String, activity: String)
    fun onDestroy(id: String, activity: String)
}