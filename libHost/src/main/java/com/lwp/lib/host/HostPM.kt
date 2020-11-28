package com.lwp.lib.host

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.*
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.UserHandle

internal class HostPM(private val orig: PackageManager, private val control: ApkControl) :
    PackageManager() {
    @Throws(NameNotFoundException::class)
    override fun getPackageInfo(packageName: String, flags: Int): PackageInfo {
        return control.packageInfo
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageInfo(versionedPackage: VersionedPackage, flags: Int): PackageInfo {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            orig.getPackageInfo(versionedPackage, flags)
        } else {
            PackageInfo()
        }
    }

    fun getPermissionControllerPackageName(): String? {
        return PackageManager::class.java.getMethod("getPermissionControllerPackageName")
            .invoke(orig) as String?
    }

//    fun getPermissionControllerPackageName(): String {
//        return orig.getPermissionControllerPackageName()
//    }

    override fun currentToCanonicalPackageNames(names: Array<String>): Array<String> {
        return orig.currentToCanonicalPackageNames(names)
    }

    override fun canonicalToCurrentPackageNames(names: Array<String>): Array<String> {
        return orig.canonicalToCurrentPackageNames(names)
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
//         val plugin =
//         ApkPlugin.getInstance().getPluginByPackageName(packageName);
        return orig.getLaunchIntentForPackage(packageName)
    }

    override fun getLeanbackLaunchIntentForPackage(packageName: String): Intent? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageGids(packageName: String): IntArray {
        return control.packageInfo.gids
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageGids(packageName: String, flags: Int): IntArray {
        return IntArray(0)
    }

    @Throws(NameNotFoundException::class)
    override fun getPackageUid(packageName: String, flags: Int): Int {
        return 0
    }

    @Throws(NameNotFoundException::class)
    override fun getPermissionInfo(name: String, flags: Int): PermissionInfo {
        return orig.getPermissionInfo(name, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun queryPermissionsByGroup(group: String, flags: Int): List<PermissionInfo> {
        return orig.queryPermissionsByGroup(group, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getPermissionGroupInfo(name: String, flags: Int): PermissionGroupInfo {
        return orig.getPermissionGroupInfo(name, flags)
    }

    override fun getAllPermissionGroups(flags: Int): List<PermissionGroupInfo> {
        return orig.getAllPermissionGroups(flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        return control.packageInfo.applicationInfo
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
        return orig.getActivityInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getReceiverInfo(component: ComponentName, flags: Int): ActivityInfo {
        return orig.getReceiverInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getServiceInfo(component: ComponentName, flags: Int): ServiceInfo {
        return orig.getServiceInfo(component, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getProviderInfo(component: ComponentName, flags: Int): ProviderInfo {
        return orig.getProviderInfo(component, flags)
    }

    override fun getInstalledPackages(flags: Int): List<PackageInfo> {
        return orig.getInstalledPackages(flags)
    }

    override fun checkPermission(permName: String, pkgName: String): Int {
        return orig.checkPermission(permName, pkgName)
    }

    override fun isPermissionRevokedByPolicy(permissionName: String, packageName: String): Boolean {
        return false
    }

    override fun addPermission(info: PermissionInfo): Boolean {
        return orig.addPermission(info)
    }

    override fun addPermissionAsync(info: PermissionInfo): Boolean {
        return orig.addPermission(info)
    }

    override fun removePermission(name: String) {
        orig.removePermission(name)
    }

    override fun checkSignatures(pkg1: String, pkg2: String): Int {
        return orig.checkSignatures(pkg1, pkg2)
    }

    override fun checkSignatures(uid1: Int, uid2: Int): Int {
        return orig.checkSignatures(uid1, uid2)
    }

    override fun getPackagesForUid(uid: Int): Array<String>? {
        return orig.getPackagesForUid(uid)
    }

    override fun getNameForUid(uid: Int): String? {
        return orig.getNameForUid(uid)
    }

    override fun getInstalledApplications(flags: Int): List<ApplicationInfo> {
        return orig.getInstalledApplications(flags)
    }

    override fun isInstantApp(): Boolean {
        return false
    }

    override fun isInstantApp(packageName: String): Boolean {
        return false
    }

    override fun getInstantAppCookieMaxBytes(): Int {
        return 0
    }

    override fun getInstantAppCookie(): ByteArray {
        return ByteArray(0)
    }

    override fun clearInstantAppCookie() {}
    override fun updateInstantAppCookie(cookie: ByteArray?) {}
    override fun getSystemSharedLibraryNames(): Array<String>? {
        return orig.systemSharedLibraryNames
    }

    @SuppressLint("NewApi")
    override fun getSharedLibraries(flags: Int): List<SharedLibraryInfo> {
        return orig.getSharedLibraries(flags)
    }

    @SuppressLint("NewApi")
    override fun getChangedPackages(sequenceNumber: Int): ChangedPackages? {
        return orig.getChangedPackages(sequenceNumber)
    }

    override fun getSystemAvailableFeatures(): Array<FeatureInfo> {
        return orig.systemAvailableFeatures
    }

    override fun hasSystemFeature(name: String): Boolean {
        return orig.hasSystemFeature(name)
    }

    override fun hasSystemFeature(featureName: String, version: Int): Boolean {
        return false
    }

    override fun resolveActivity(intent: Intent, flags: Int): ResolveInfo? {
        return orig.resolveActivity(intent, flags)
    }

    override fun queryIntentActivities(intent: Intent, flags: Int): List<ResolveInfo> {
        return orig.queryIntentActivities(intent, flags)
    }

    override fun queryIntentActivityOptions(
        caller: ComponentName?,
        specifics: Array<Intent>?, intent: Intent, flags: Int
    ): List<ResolveInfo> {
        return orig
            .queryIntentActivityOptions(caller, specifics, intent, flags)
    }

    override fun queryBroadcastReceivers(intent: Intent, flags: Int): List<ResolveInfo> {
        return orig.queryBroadcastReceivers(intent, flags)
    }

    override fun resolveService(intent: Intent, flags: Int): ResolveInfo? {
        return orig.resolveService(intent, flags)
    }

    override fun queryIntentServices(intent: Intent, flags: Int): List<ResolveInfo> {
        return orig.queryIntentServices(intent, flags)
    }

    override fun resolveContentProvider(name: String, flags: Int): ProviderInfo? {
        return orig.resolveContentProvider(name, flags)
    }

    override fun queryContentProviders(
        processName: String,
        uid: Int, flags: Int
    ): List<ProviderInfo> {
        return orig.queryContentProviders(processName, uid, flags)
    }

    @Throws(NameNotFoundException::class)
    override fun getInstrumentationInfo(
        className: ComponentName,
        flags: Int
    ): InstrumentationInfo {
        return orig.getInstrumentationInfo(className, flags)
    }

    override fun queryInstrumentation(
        targetPackage: String,
        flags: Int
    ): List<InstrumentationInfo> {
        return orig.queryInstrumentation(targetPackage, flags)
    }

    override fun getDrawable(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): Drawable? {
        return orig.getDrawable(packageName, resid, appInfo)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityIcon(activityName: ComponentName): Drawable? {
        return orig.getActivityIcon(activityName)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityIcon(intent: Intent): Drawable {
        return orig.getActivityIcon(intent)
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityBanner(activityName: ComponentName): Drawable? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityBanner(intent: Intent): Drawable? {
        return null
    }

    override fun getDefaultActivityIcon(): Drawable {
        return orig.defaultActivityIcon
    }

    override fun getApplicationIcon(info: ApplicationInfo): Drawable {
        return orig.getApplicationIcon(info)
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationIcon(packageName: String): Drawable? {
        return orig.getApplicationIcon(packageName)
    }

    override fun getApplicationBanner(info: ApplicationInfo): Drawable? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationBanner(packageName: String): Drawable? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityLogo(activityName: ComponentName): Drawable? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getActivityLogo(intent: Intent): Drawable? {
        return null
    }

    override fun getApplicationLogo(info: ApplicationInfo): Drawable? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getApplicationLogo(packageName: String): Drawable? {
        return null
    }

    override fun getUserBadgedIcon(drawable: Drawable, user: UserHandle): Drawable? {
        return null
    }

    override fun getUserBadgedDrawableForDensity(
        drawable: Drawable,
        user: UserHandle,
        badgeLocation: Rect,
        badgeDensity: Int
    ): Drawable? {
        return null
    }

    override fun getUserBadgedLabel(label: CharSequence, user: UserHandle): CharSequence? {
        return null
    }

    override fun getText(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): CharSequence? {
        return null
    }

    override fun getXml(
        packageName: String, resid: Int,
        appInfo: ApplicationInfo
    ): XmlResourceParser? {
        return null
    }

    override fun getApplicationLabel(info: ApplicationInfo): CharSequence? {
        return null
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForActivity(activityName: ComponentName): Resources? {
        return orig.getResourcesForApplication(activityName.packageName)
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForApplication(app: ApplicationInfo): Resources? {
        return orig.getResourcesForApplication(app)
    }

    @Throws(NameNotFoundException::class)
    override fun getResourcesForApplication(appPackageName: String): Resources? {
        return orig.getResourcesForApplication(appPackageName)
    }

    override fun getInstallerPackageName(packageName: String): String? {
        return null
    }

    override fun addPackageToPreferred(packageName: String) {
    }

    override fun removePackageFromPreferred(packageName: String) {
    }

    override fun getPreferredPackages(flags: Int): List<PackageInfo>? {
        return null
    }

    override fun addPreferredActivity(
        filter: IntentFilter, match: Int,
        set: Array<ComponentName>, activity: ComponentName
    ) {
    }

    override fun clearPackagePreferredActivities(packageName: String) {
    }

    override fun getPreferredActivities(
        outFilters: List<IntentFilter>,
        outActivities: List<ComponentName>, packageName: String
    ): Int {
        return 0
    }

    override fun setComponentEnabledSetting(
        componentName: ComponentName,
        newState: Int, flags: Int
    ) {
    }

    override fun getComponentEnabledSetting(componentName: ComponentName): Int {
        return orig.getComponentEnabledSetting(componentName)
    }

    override fun setApplicationEnabledSetting(
        packageName: String, newState: Int,
        flags: Int
    ) {
    }

    override fun getApplicationEnabledSetting(packageName: String): Int {
        return orig.getApplicationEnabledSetting(packageName)
    }

    override fun isSafeMode(): Boolean {
        return orig.isSafeMode
    }

    override fun setApplicationCategoryHint(packageName: String, categoryHint: Int) {}

    @SuppressLint("NewApi")
    override fun getPackageInstaller(): PackageInstaller {
        return orig.packageInstaller
    }

    @SuppressLint("NewApi")
    override fun canRequestPackageInstalls(): Boolean {
        return orig.canRequestPackageInstalls()
    }

    override fun getPackagesHoldingPermissions(
        permissions: Array<String>,
        flags: Int
    ): List<PackageInfo>? {
        return null
    }

    override fun queryIntentContentProviders(intent: Intent, flags: Int): List<ResolveInfo>? {
        return null
    }

    override fun verifyPendingInstall(id: Int, verificationCode: Int) {
    }

    override fun extendVerificationTimeout(
        id: Int,
        verificationCodeAtTimeout: Int,
        millisecondsToDelay: Long
    ) {
    }

    override fun setInstallerPackageName(targetPackage: String, installerPackageName: String) {
    }
}