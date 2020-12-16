package com.lwp.lib.host.hook

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.*
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.UserHandle
import com.lwp.lib.host.findApkInfo
import com.lwp.lib.host.hostActivityInfo
import com.lwp.lib.host.hostComponentName
import com.lwp.lib.host.pluginActivity

internal class HookPM(private val poxy: PackageManager) :
    PackageManager() {
    @Throws(NameNotFoundException::class)
    override fun getPackageInfo(packageName: String, flags: Int): PackageInfo {
        return findApkInfo(packageName)?.packageInfo ?: poxy.getPackageInfo(packageName, flags)
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Throws(NameNotFoundException::class)
    override fun getPackageInfo(versionedPackage: VersionedPackage, flags: Int): PackageInfo {
        return findApkInfo(versionedPackage.packageName)?.packageInfo ?: poxy.getPackageInfo(
            versionedPackage,
            flags
        )
    }

    fun getPermissionControllerPackageName(): String? {
        return PackageManager::class.java.getMethod("getPermissionControllerPackageName")
            .invoke(poxy) as String?
    }

//    fun getPermissionControllerPackageName(): String {
//        return orig.getPermissionControllerPackageName()
//    }

    override fun currentToCanonicalPackageNames(names: Array<String>): Array<String> {
        return poxy.currentToCanonicalPackageNames(names)
    }

    override fun canonicalToCurrentPackageNames(names: Array<String>): Array<String> {
        return poxy.canonicalToCurrentPackageNames(names)
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
//         val plugin =
//         ApkPlugin.getInstance().getPluginByPackageName(packageName);
        return poxy.getLaunchIntentForPackage(packageName)
    }

    override fun getLeanbackLaunchIntentForPackage(packageName: String): Intent? {
        return poxy.getLeanbackLaunchIntentForPackage(packageName)
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageGids(packageName: String): IntArray {
        return poxy.getPackageGids(hostComponentName.packageName)
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Throws(NameNotFoundException::class)
    override fun getPackageGids(packageName: String, flags: Int): IntArray {
        return findApkInfo(packageName)?.packageInfo?.gids ?: poxy.getPackageGids(
            packageName,
            flags
        )
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageUid(packageName: String, flags: Int): Int {
        return 0
    }

    @Throws(NameNotFoundException::class)
    override fun getPermissionInfo(name: String, flags: Int): PermissionInfo {
        return poxy.getPermissionInfo(name, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun queryPermissionsByGroup(group: String, flags: Int): List<PermissionInfo> {
        return poxy.queryPermissionsByGroup(group, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getPermissionGroupInfo(name: String, flags: Int): PermissionGroupInfo {
        return poxy.getPermissionGroupInfo(name, flags)
    }

    override fun getAllPermissionGroups(flags: Int): List<PermissionGroupInfo> {
        return poxy.getAllPermissionGroups(flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        return findApkInfo(packageName)?.mApplicationInfo ?: poxy.getApplicationInfo(
            packageName,
            flags
        )
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
        return if (component.className == pluginActivity) {
            return hostActivityInfo
        } else poxy.getActivityInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getReceiverInfo(component: ComponentName, flags: Int): ActivityInfo {
        return poxy.getReceiverInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getServiceInfo(component: ComponentName, flags: Int): ServiceInfo {
        return poxy.getServiceInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getProviderInfo(component: ComponentName, flags: Int): ProviderInfo {
        return poxy.getProviderInfo(component, flags)
    }

    override fun getInstalledPackages(flags: Int): List<PackageInfo> {
        return poxy.getInstalledPackages(flags)
    }

    override fun checkPermission(permName: String, pkgName: String): Int {
        return poxy.checkPermission(permName, pkgName)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun isPermissionRevokedByPolicy(permissionName: String, packageName: String): Boolean {
        return poxy.isPermissionRevokedByPolicy(permissionName, packageName)
    }

    override fun addPermission(info: PermissionInfo): Boolean {
        return poxy.addPermission(info)
    }

    override fun addPermissionAsync(info: PermissionInfo): Boolean {
        return poxy.addPermission(info)
    }

    override fun removePermission(name: String) {
        poxy.removePermission(name)
    }

    override fun checkSignatures(pkg1: String, pkg2: String): Int {
        return poxy.checkSignatures(pkg1, pkg2)
    }

    override fun checkSignatures(uid1: Int, uid2: Int): Int {
        return poxy.checkSignatures(uid1, uid2)
    }

    override fun getPackagesForUid(uid: Int): Array<String>? {
        return poxy.getPackagesForUid(uid)
    }

    override fun getNameForUid(uid: Int): String? {
        return poxy.getNameForUid(uid)
    }

    override fun getInstalledApplications(flags: Int): List<ApplicationInfo> {
        return poxy.getInstalledApplications(flags)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun isInstantApp(): Boolean {
        return poxy.isInstantApp
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun isInstantApp(packageName: String): Boolean {
        return findApkInfo(packageName) != null || poxy.isInstantApp(packageName)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getInstantAppCookieMaxBytes(): Int {
        return poxy.instantAppCookieMaxBytes
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getInstantAppCookie(): ByteArray {
        return poxy.instantAppCookie
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun clearInstantAppCookie() {
        poxy.clearInstantAppCookie()
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun updateInstantAppCookie(cookie: ByteArray?) {
        poxy.updateInstantAppCookie(cookie)
    }

    override fun getSystemSharedLibraryNames(): Array<String>? {
        return poxy.systemSharedLibraryNames
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getSharedLibraries(flags: Int): List<SharedLibraryInfo> {
        return poxy.getSharedLibraries(flags)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getChangedPackages(sequenceNumber: Int): ChangedPackages? {
        return poxy.getChangedPackages(sequenceNumber)
    }

    override fun getSystemAvailableFeatures(): Array<FeatureInfo> {
        return poxy.systemAvailableFeatures
    }

    override fun hasSystemFeature(name: String): Boolean {
        return poxy.hasSystemFeature(name)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun hasSystemFeature(featureName: String, version: Int): Boolean {
        return poxy.hasSystemFeature(featureName, version)
    }

    override fun resolveActivity(intent: Intent, flags: Int): ResolveInfo? {
        return poxy.resolveActivity(intent, flags)
    }

    override fun queryIntentActivities(intent: Intent, flags: Int): List<ResolveInfo> {
        return poxy.queryIntentActivities(intent, flags)
    }

    override fun queryIntentActivityOptions(
        caller: ComponentName?,
        specifics: Array<Intent>?, intent: Intent, flags: Int
    ): List<ResolveInfo> {
        return poxy
            .queryIntentActivityOptions(caller, specifics, intent, flags)
    }

    override fun queryBroadcastReceivers(intent: Intent, flags: Int): List<ResolveInfo> {
        return poxy.queryBroadcastReceivers(intent, flags)
    }

    override fun resolveService(intent: Intent, flags: Int): ResolveInfo? {
        return poxy.resolveService(intent, flags)
    }

    override fun queryIntentServices(intent: Intent, flags: Int): List<ResolveInfo> {
        return poxy.queryIntentServices(intent, flags)
    }

    override fun resolveContentProvider(name: String, flags: Int): ProviderInfo? {
        return poxy.resolveContentProvider(name, flags)
    }

    override fun queryContentProviders(
        processName: String,
        uid: Int, flags: Int
    ): List<ProviderInfo> {
        return poxy.queryContentProviders(processName, uid, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getInstrumentationInfo(
        className: ComponentName,
        flags: Int
    ): InstrumentationInfo {
        return poxy.getInstrumentationInfo(className, flags)
    }

    override fun queryInstrumentation(
        targetPackage: String,
        flags: Int
    ): List<InstrumentationInfo> {
        return poxy.queryInstrumentation(targetPackage, flags)
    }

    override fun getDrawable(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): Drawable? {
        return poxy.getDrawable(packageName, resid, appInfo)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityIcon(activityName: ComponentName): Drawable? {
        return poxy.getActivityIcon(activityName)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityIcon(intent: Intent): Drawable {
        return poxy.getActivityIcon(intent)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityBanner(activityName: ComponentName): Drawable? {
        return poxy.getActivityBanner(activityName)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityBanner(intent: Intent): Drawable? {
        return poxy.getActivityBanner(intent)
    }

    override fun getDefaultActivityIcon(): Drawable {
        return poxy.defaultActivityIcon
    }

    override fun getApplicationIcon(info: ApplicationInfo): Drawable {
        return poxy.getApplicationIcon(info)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationIcon(packageName: String): Drawable? {
        return poxy.getApplicationIcon(packageName)
    }

    override fun getApplicationBanner(info: ApplicationInfo): Drawable? {
        return poxy.getApplicationBanner(info)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationBanner(packageName: String): Drawable? {
        return poxy.getApplicationBanner(packageName)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityLogo(activityName: ComponentName): Drawable? {
        return poxy.getActivityLogo(activityName)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityLogo(intent: Intent): Drawable? {
        return poxy.getActivityLogo(intent)
    }

    override fun getApplicationLogo(info: ApplicationInfo): Drawable? {
        return poxy.getApplicationLogo(info)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationLogo(packageName: String): Drawable? {
        return poxy.getApplicationLogo(packageName)
    }

    override fun getUserBadgedIcon(drawable: Drawable, user: UserHandle): Drawable? {
        return poxy.getUserBadgedIcon(drawable, user)
    }

    override fun getUserBadgedDrawableForDensity(
        drawable: Drawable,
        user: UserHandle,
        badgeLocation: Rect,
        badgeDensity: Int
    ): Drawable? {
        return poxy.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity)
    }

    override fun getUserBadgedLabel(label: CharSequence, user: UserHandle): CharSequence? {
        return poxy.getUserBadgedLabel(label, user)
    }

    override fun getText(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): CharSequence? {
        return poxy.getText(packageName, resid, appInfo)
    }

    override fun getXml(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): XmlResourceParser? {
        return poxy.getXml(packageName, resid, appInfo)
    }

    override fun getApplicationLabel(info: ApplicationInfo): CharSequence? {
        return poxy.getApplicationLabel(info)
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForActivity(activityName: ComponentName): Resources? {
        return poxy.getResourcesForApplication(activityName.packageName)
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForApplication(app: ApplicationInfo): Resources? {
        return poxy.getResourcesForApplication(app)
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForApplication(appPackageName: String): Resources? {
        return poxy.getResourcesForApplication(appPackageName)
    }

    override fun getInstallerPackageName(packageName: String): String? {
        return poxy.getInstallerPackageName(packageName)
    }

    override fun addPackageToPreferred(packageName: String) {
        poxy.addPackageToPreferred(packageName)
    }

    override fun removePackageFromPreferred(packageName: String) {
        poxy.removePackageFromPreferred(packageName)
    }

    override fun getPreferredPackages(flags: Int): List<PackageInfo>? {
        return poxy.getPreferredPackages(flags)
    }

    override fun addPreferredActivity(
        filter: IntentFilter, match: Int,
        set: Array<ComponentName>, activity: ComponentName
    ) {
        poxy.addPreferredActivity(filter, match, set, activity)
    }

    override fun clearPackagePreferredActivities(packageName: String) {
        poxy.clearPackagePreferredActivities(packageName)
    }

    override fun getPreferredActivities(
        outFilters: List<IntentFilter>,
        outActivities: List<ComponentName>, packageName: String
    ): Int {
        return poxy.getPreferredActivities(outFilters, outActivities, packageName)
    }

    override fun setComponentEnabledSetting(
        componentName: ComponentName,
        newState: Int, flags: Int
    ) {
        poxy.setComponentEnabledSetting(componentName, newState, flags)
    }

    override fun getComponentEnabledSetting(componentName: ComponentName): Int {
        return poxy.getComponentEnabledSetting(componentName)
    }

    override fun setApplicationEnabledSetting(
        packageName: String, newState: Int,
        flags: Int
    ) {
        poxy.setApplicationEnabledSetting(packageName, newState, flags)
    }

    override fun getApplicationEnabledSetting(packageName: String): Int {
        return poxy.getApplicationEnabledSetting(packageName)
    }

    override fun isSafeMode(): Boolean {
        return poxy.isSafeMode
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun setApplicationCategoryHint(packageName: String, categoryHint: Int) {
        poxy.setApplicationCategoryHint(packageName, categoryHint)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun getPackageInstaller(): PackageInstaller {
        return poxy.packageInstaller
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun canRequestPackageInstalls(): Boolean {
        return poxy.canRequestPackageInstalls()
    }

    override fun getPackagesHoldingPermissions(
        permissions: Array<String>,
        flags: Int
    ): List<PackageInfo>? {
        return poxy.getPackagesHoldingPermissions(permissions, flags)
    }

    override fun queryIntentContentProviders(intent: Intent, flags: Int): List<ResolveInfo>? {
        return poxy.queryIntentContentProviders(intent, flags)
    }

    override fun verifyPendingInstall(id: Int, verificationCode: Int) {
        poxy.verifyPendingInstall(id, verificationCode)
    }

    override fun extendVerificationTimeout(
        id: Int,
        verificationCodeAtTimeout: Int,
        millisecondsToDelay: Long
    ) {
        poxy.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay)
    }

    override fun setInstallerPackageName(targetPackage: String, installerPackageName: String) {
        poxy.setInstallerPackageName(targetPackage, installerPackageName)
    }
}