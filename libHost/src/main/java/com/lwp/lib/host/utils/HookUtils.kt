package com.lwp.lib.host.utils

import com.lwp.lib.host.HostManager
import com.lwp.lib.host.hook.HookPMS
import com.lwp.lib.host.hook.hookAbout
import com.lwp.lib.host.hook.hookInstrumentation

fun hook() {
    hookAbout("android.app.ActivityThread") {
        it.getField("sCurrentActivityThread", null)
    }
    hookInstrumentation()
    HookPMS.hookPMS(HostManager.application())
}