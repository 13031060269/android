package com.lwp.lib.host.hook

import android.annotation.TargetApi
import android.app.Instrumentation.ActivityMonitor
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.lwp.lib.host.HostManager
import com.lwp.lib.host.KEY_PLUGIN
import com.lwp.lib.host.hostComponentName
import com.lwp.lib.host.utils.formatComponent
import java.lang.reflect.Field

class HookActivityMonitor : ActivityMonitor {

    private constructor(cls: String?, result: ActivityResult?, block: Boolean) : super(
        cls,
        result,
        block
    ) {
//        try {
//            (Class::class.java.getDeclaredMethod(
//                "getDeclaredFieldsUnchecked",
//                Boolean::class.javaPrimitiveType
//            ).invoke(ActivityMonitor::class.java, false) as Array<Field>)
//                .forEach {
//                    if (it.name == "mIgnoreMatchingSpecificIntents") {
//                        it.isAccessible = true
//                        it.set(this, true)
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private constructor() : super()

    companion object {
        val instance: HookActivityMonitor by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                HookActivityMonitor()
            } else {
                HookActivityMonitor(null, null, false)
            }
        }
    }

    override fun onStartActivity(intent: Intent?): ActivityResult? {
        intent?.component?.apply {
            HostManager.findActivityInfo(this)?.also {
                intent.putExtra(KEY_PLUGIN, formatComponent(this))
                intent.component = hostComponentName
            }
        }
        return null
    }
}