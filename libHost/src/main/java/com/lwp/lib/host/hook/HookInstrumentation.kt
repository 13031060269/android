package com.lwp.lib.host.hook

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.*
import android.view.KeyEvent
import android.view.MotionEvent
import com.lwp.lib.host.KEY_PLUGIN
import com.lwp.lib.host.findApkInfo
import com.lwp.lib.host.utils.parseComponent


fun hookInstrumentation() {
    val activityThread = hookAbout("android.app.ActivityThread")
    val instrumentation = HookInstrumentation(activityThread.getField("mInstrumentation"))
    activityThread.setField("mInstrumentation", instrumentation)
}

class HookInstrumentation(private val poxy: Instrumentation) : Instrumentation() {
    init {
        addMonitor(HookActivityMonitor.instance)
    }

    override fun newActivity(
        clazz: Class<*>?,
        context: Context?,
        token: IBinder?,
        application: Application?,
        intent: Intent?,
        info: ActivityInfo?,
        title: CharSequence?,
        parent: Activity?,
        id: String?,
        lastNonConfigurationInstance: Any?
    ): Activity {
        return poxy.newActivity(
            clazz,
            context,
            token,
            application,
            intent,
            info,
            title,
            parent,
            id,
            lastNonConfigurationInstance
        )
    }


    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity {
        try {
            parseComponent(intent?.getStringExtra(KEY_PLUGIN))?.apply {
                return findApkInfo(packageName)!!.loadClass(this.className)!!
                    .newInstance() as Activity
            }
        } catch (e: Exception) {
        }
        return poxy.newActivity(cl, className, intent)
    }


    override fun onCreate(arguments: Bundle?) {
        poxy.onCreate(arguments)
    }

    override fun start() {
        poxy.start()
    }

    override fun onStart() {
        poxy.onStart()
    }

    override fun onException(obj: Any?, e: Throwable?): Boolean {
        return poxy.onException(obj, e)
    }

    override fun sendStatus(resultCode: Int, results: Bundle?) {
        poxy.sendStatus(resultCode, results)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun addResults(results: Bundle?) {
        poxy.addResults(results)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        poxy.finish(resultCode, results)
    }

    override fun setAutomaticPerformanceSnapshots() {
        poxy.setAutomaticPerformanceSnapshots()
    }

    override fun startPerformanceSnapshot() {
        poxy.startPerformanceSnapshot()
    }

    override fun endPerformanceSnapshot() {
        poxy.endPerformanceSnapshot()
    }

    override fun onDestroy() {
        poxy.onDestroy()
    }

    override fun getContext(): Context {
        return poxy.context
    }

    override fun getComponentName(): ComponentName {
        return poxy.componentName
    }

    override fun getTargetContext(): Context {
        return poxy.targetContext
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getProcessName(): String {
        return poxy.processName
    }

    override fun isProfiling(): Boolean {
        return poxy.isProfiling
    }

    override fun startProfiling() {
        poxy.startProfiling()
    }

    override fun stopProfiling() {
        poxy.stopProfiling()
    }

    override fun setInTouchMode(inTouch: Boolean) {
        poxy.setInTouchMode(inTouch)
    }

    override fun waitForIdle(recipient: Runnable?) {
        poxy.waitForIdle(recipient)
    }

    override fun waitForIdleSync() {
        poxy.waitForIdleSync()
    }

    override fun runOnMainSync(runner: Runnable?) {
        poxy.runOnMainSync(runner)
    }

    override fun startActivitySync(intent: Intent?): Activity {
        return poxy.startActivitySync(intent)
    }

    @TargetApi(Build.VERSION_CODES.P)
    override fun startActivitySync(intent: Intent, options: Bundle?): Activity {
        return poxy.startActivitySync(intent, options)
    }

//    override fun addMonitor(monitor: ActivityMonitor?) {
//        poxy.addMonitor(monitor)
//    }
//
//    override fun addMonitor(
//        filter: IntentFilter?,
//        result: ActivityResult?,
//        block: Boolean
//    ): ActivityMonitor {
//        return poxy.addMonitor(filter, result, block)
//    }
//
//    override fun addMonitor(
//        cls: String?,
//        result: ActivityResult?,
//        block: Boolean
//    ): ActivityMonitor {
//        return poxy.addMonitor(cls, result, block)
//    }

    override fun checkMonitorHit(monitor: ActivityMonitor?, minHits: Int): Boolean {
        return poxy.checkMonitorHit(monitor, minHits)
    }

    override fun waitForMonitor(monitor: ActivityMonitor?): Activity {
        return poxy.waitForMonitor(monitor)
    }

    override fun waitForMonitorWithTimeout(monitor: ActivityMonitor?, timeOut: Long): Activity {
        return poxy.waitForMonitorWithTimeout(monitor, timeOut)
    }

    override fun removeMonitor(monitor: ActivityMonitor?) {
        poxy.removeMonitor(monitor)
    }

    override fun invokeMenuActionSync(targetActivity: Activity?, id: Int, flag: Int): Boolean {
        return poxy.invokeMenuActionSync(targetActivity, id, flag)
    }

    override fun invokeContextMenuAction(targetActivity: Activity?, id: Int, flag: Int): Boolean {
        return poxy.invokeContextMenuAction(targetActivity, id, flag)
    }

    override fun sendStringSync(text: String?) {
        poxy.sendStringSync(text)
    }

    override fun sendKeySync(event: KeyEvent?) {
        poxy.sendKeySync(event)
    }

    override fun sendKeyDownUpSync(key: Int) {
        poxy.sendKeyDownUpSync(key)
    }

    override fun sendCharacterSync(keyCode: Int) {
        poxy.sendCharacterSync(keyCode)
    }

    override fun sendPointerSync(event: MotionEvent?) {
        poxy.sendPointerSync(event)
    }

    override fun sendTrackballEventSync(event: MotionEvent?) {
        poxy.sendTrackballEventSync(event)
    }

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return poxy.newApplication(cl, className, context)
    }

    override fun callApplicationOnCreate(app: Application?) {
        poxy.callApplicationOnCreate(app)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
        poxy.callActivityOnCreate(activity, icicle)
    }

    override fun callActivityOnCreate(
        activity: Activity,
        icicle: Bundle?,
        persistentState: PersistableBundle?
    ) {
        poxy.callActivityOnCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnDestroy(activity: Activity?) {
        poxy.callActivityOnDestroy(activity)
    }

    override fun callActivityOnRestoreInstanceState(
        activity: Activity?,
        savedInstanceState: Bundle?
    ) {
        poxy.callActivityOnRestoreInstanceState(activity, savedInstanceState)
    }

    override fun callActivityOnRestoreInstanceState(
        activity: Activity?,
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        poxy.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnPostCreate(activity: Activity?, icicle: Bundle?) {
        poxy.callActivityOnPostCreate(activity, icicle)
    }

    override fun callActivityOnPostCreate(
        activity: Activity?,
        icicle: Bundle?,
        persistentState: PersistableBundle?
    ) {
        poxy.callActivityOnPostCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnNewIntent(activity: Activity?, intent: Intent?) {
        poxy.callActivityOnNewIntent(activity, intent)
    }

    override fun callActivityOnStart(activity: Activity?) {
        poxy.callActivityOnStart(activity)
    }

    override fun callActivityOnRestart(activity: Activity?) {
        poxy.callActivityOnRestart(activity)
    }

    override fun callActivityOnResume(activity: Activity?) {
        poxy.callActivityOnResume(activity)
    }

    override fun callActivityOnStop(activity: Activity?) {
        poxy.callActivityOnStop(activity)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity?, outState: Bundle?) {
        poxy.callActivityOnSaveInstanceState(activity, outState)
    }

    override fun callActivityOnSaveInstanceState(
        activity: Activity?,
        outState: Bundle?,
        outPersistentState: PersistableBundle?
    ) {
        poxy.callActivityOnSaveInstanceState(activity, outState, outPersistentState)
    }

    override fun callActivityOnPause(activity: Activity?) {
        poxy.callActivityOnPause(activity)
    }

    override fun callActivityOnUserLeaving(activity: Activity?) {
        poxy.callActivityOnUserLeaving(activity)
    }

    override fun startAllocCounting() {
        poxy.startAllocCounting()
    }

    override fun stopAllocCounting() {
        poxy.stopAllocCounting()
    }

    override fun getAllocCounts(): Bundle {
        return poxy.allocCounts
    }

    override fun getBinderCounts(): Bundle {
        return poxy.binderCounts
    }

    override fun getUiAutomation(): UiAutomation {
        return poxy.uiAutomation
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getUiAutomation(flags: Int): UiAutomation {
        return poxy.getUiAutomation(flags)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun acquireLooperManager(looper: Looper?): TestLooperManager {
        return poxy.acquireLooperManager(looper)
    }
}