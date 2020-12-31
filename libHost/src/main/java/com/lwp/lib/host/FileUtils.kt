package com.lwp.lib.host

import android.content.Context
import java.io.File

const val libPath = "/lib"
const val activitiesPath = "/activities/"
const val apkPath = "/base.apk"
const val optimized = "/optimized"
const val pluginActivity = "com.lwp.lib.host.PluginActivity"
private lateinit var outputPath: String

private const val dir = "_dir"
fun initFirst(context: Context) {
    val apkList = context.getDir("apkList", Context.MODE_PRIVATE)
    apkList.mkdirs()
    outputPath = apkList.absolutePath
}

fun deleteFile(file: File) {
    if (file.exists() && file.isFile) {
        file.delete()
    } else {
        file.listFiles()?.forEach {
            it?.run {
                deleteFile(this)
            }
        }
    }
}

fun dir(id: String) = "$outputPath/$id$dir"

fun clear(id: String, pkg: String) {
    val dir = File(dir(id))
    if (dir.exists()) {
        deleteFile(dir)
    }
    val file = File(HostManager.application().cacheDir.parent, "shared_prefs")
    if (file.exists()) {
        val list = file.listFiles { _, name -> name.startsWith(pkg) }
        list?.forEach {
            it?.delete()
        }
    }
}

fun deleteFile(fileName: String) {
    deleteFile(File(fileName))
}