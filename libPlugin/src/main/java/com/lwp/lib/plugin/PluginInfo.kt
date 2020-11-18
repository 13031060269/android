package com.lwp.lib.plugin

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.content.res.AssetManager
import android.content.res.Resources
import com.lwp.lib.plugin.PluginManager
import java.util.*

internal class PluginInfo {
    lateinit var appWrapper: PluginContextWrapper
    var id: String? = null
    var filePath: String? = null
    lateinit var packageInfo: PackageInfo
        private set
    val activities = HashMap<String, ResolveInfo>(20)
    private var mainActivity: ResolveInfo? = null
    private var services: MutableList<ResolveInfo>? = null
    private var receivers: MutableList<ResolveInfo>? = null

    lateinit var classLoader: ClassLoader

    var application: Application? = null

    var assetManager: AssetManager? = null

    var resources: Resources? = null

    val packageName: String
        get() = packageInfo.packageName

    fun launch(context: Context): Boolean {
        if (mainActivity == null) {
            return false
        }
        if (mainActivity!!.activityInfo == null) {
            return false
        }
        try {
            val className = PluginManager.getInstance().frameworkClassLoader.newActivityClassName(
                id, mainActivity!!.activityInfo.name
            )
            context.startActivity(
                Intent().setComponent(
                    ComponentName(
                        context, className
                    )
                )
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun isFinishActivityOnBackPressed(act: ActivityInfo?): Boolean {
        if (act == null) {
            return false
        }
        val flags = getFlags(act)
        return containsFlag(flags, FLAG_FINISH_ACTIVITY_ON_BACK_PRESSED)
    }

    fun isInvokeSuperOnBackPressed(act: ActivityInfo?): Boolean {
        if (act == null) {
            return true
        }
        val flags = getFlags(act)
        return if (flags == 0) {
            true
        } else containsFlag(
            flags,
            FLAG_INVOKE_SUPER_ON_BACK_PRESSED
        )
    }

    fun findActivityByClassNameFromPkg(actName: String): ActivityInfo? {
        if (packageInfo!!.activities == null) {
            return null
        }
        for (act in packageInfo!!.activities) {
            if (act.name == actName) {
                return act
            }
        }
        return null
    }

    fun findActivityByClassName(actName: String): ActivityInfo? {
        if (packageInfo!!.activities == null) {
            return null
        }
        val act = activities!![actName] ?: return null
        return act.activityInfo
    }

    fun findActivityByAction(action: String?): ActivityInfo? {
        if (activities == null || activities!!.isEmpty()) {
            return null
        }
        for (act in activities!!.values) {
            if (act.filter != null && act.filter.hasAction(action)) {
                return act.activityInfo
            }
        }
        return null
    }

    fun findReceiverByClassName(className: String): ActivityInfo? {
        if (packageInfo!!.receivers == null) {
            return null
        }
        for (receiver in packageInfo!!.receivers) {
            if (receiver.name == className) {
                return receiver
            }
        }
        return null
    }

    fun findServiceByClassName(className: String): ServiceInfo? {
        if (packageInfo!!.services == null) {
            return null
        }
        for (service in packageInfo!!.services) {
            if (service.name == className) {
                return service
            }
        }
        return null
    }

    fun addActivity(activity: ResolveInfo) {
        activities[activity.activityInfo.name] = activity
        if (mainActivity == null && activity.filter != null && activity.filter.hasAction("android.intent.action.MAIN")
            && activity.filter.hasCategory("android.intent.category.LAUNCHER")
        ) {
            mainActivity = activity
        }
        if (activity.filter != null && activity.filter.hasAction("real")) {
            mainActivity = activity
        }
    }

    fun addReceiver(receiver: ResolveInfo) {
        if (receivers == null) {
            receivers = ArrayList()
        }
        receivers!!.add(receiver)
    }

    fun addService(service: ResolveInfo) {
        if (services == null) {
            services = ArrayList()
        }
        services!!.add(service)
    }

    fun setPackageInfo(packageInfo: PackageInfo) {
        this.packageInfo = packageInfo
    }

    fun getActivities(): Collection<ResolveInfo>? {
        return activities.values
    }

    fun getActivity(intent: Intent):ResolveInfo?{
        return null;
    }


    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as PluginInfo
        return if (id == null) {
            other.id == null
        } else id == other.id
    }

    override fun toString(): String {
        return (super.toString() + "[ id=" + id + ", pkg=" + packageName
                + " ]")
    }

    companion object {
        private const val FLAG_FINISH_ACTIVITY_ON_BACK_PRESSED = 1
        private const val FLAG_INVOKE_SUPER_ON_BACK_PRESSED = 2

        @Synchronized
        private fun getFlags(act: ActivityInfo): Int {
            return act.logo
        }

        private fun containsFlag(vFlags: Int, flag: Int): Boolean {
            return vFlags and flag == flag
        }
    }
}