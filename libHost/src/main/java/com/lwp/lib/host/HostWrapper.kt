package com.lwp.lib.host

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.view.ContextThemeWrapper
import java.io.File

internal class HostWrapper(
    appWrapper: ApkInfo.HostContextWrapper,
    apkInfo: ActivityInfo
) :
    ContextThemeWrapper(appWrapper, apkInfo.theme)