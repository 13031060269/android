package com.lwp.lib.pluginhostpublic

interface PluginLifeCycle {
    fun onCreate(id: String)
    fun onDestroy(id: String)
}