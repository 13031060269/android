package com.lwp.lib.host

import android.content.pm.ActivityInfo
import android.view.ContextThemeWrapper

internal class ActivityWrapper(
    appWrapper: ApkControl.HostContextWrapper,
    apkInfo: ActivityInfo
) :
    ContextThemeWrapper(appWrapper, apkInfo.theme)