package com.lwp.lib.host.hook

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import com.lwp.lib.host.HostManager
import com.lwp.lib.host.KEY_PLUGIN
import com.lwp.lib.host.hostComponentName
import com.lwp.lib.host.utils.formatComponent

interface MonitorInterface {
    fun onStartActivity(intent: Intent?): ActivityResult? {
        intent?.component?.apply {
            HostManager.findActivityInfo(this)?.also {
                intent.putExtra(KEY_PLUGIN, formatComponent(this))
                intent.component = hostComponentName
            }
        }
        return null
    }
}