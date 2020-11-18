package com.lwp.lib.plugin

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.lwp.lib.plugin.ActivityOverrider.Companion.getPluginLibDir
import com.lwp.lib.plugin.PluginUtils.saveToFile
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

internal object PluginManifestUtil {
    @JvmStatic
    @Throws(XmlPullParserException::class, IOException::class)
    fun setManifestInfo(context: Context, apkPath: String, info: PluginInfo) {
        val zipFile = ZipFile(File(apkPath), ZipFile.OPEN_READ)
        val manifestXmlEntry = zipFile.getEntry(XmlManifestReader.DEFAULT_XML)
        val manifestXML = XmlManifestReader.getManifestXMLFromAPK(
            zipFile,
            manifestXmlEntry
        )
        val pkgInfo = context.packageManager
            .getPackageArchiveInfo(
                apkPath,
                PackageManager.GET_ACTIVITIES
                        or PackageManager.GET_RECEIVERS //
                        or PackageManager.GET_PROVIDERS //
                        or PackageManager.GET_META_DATA //
                        or PackageManager.GET_SHARED_LIBRARY_FILES //
                        or PackageManager.GET_SERVICES //
                //                                | PackageManager.GET_SIGNATURES//
            )
        pkgInfo?.apply {
            info.setPackageInfo(this)
            val libDir = getPluginLibDir(info.id)
            zipFile.use {
                if (extractLibFile(it, libDir)) {
                    this.applicationInfo.nativeLibraryDir = libDir.absolutePath
                }
            }
            setAttrs(info, manifestXML)
        }
    }

    @Throws(IOException::class)
    private fun extractLibFile(zip: ZipFile, tarDir: File): Boolean {
//        val defaultArch = "arm64-v8a"
        val defaultArch = "x86"
        val archLibEntries: MutableMap<String, MutableList<ZipEntry>> = HashMap()
        val e = zip.entries()
        while (e
                .hasMoreElements()
        ) {
            val entry = e.nextElement()
            var name = entry.name
            if (name.startsWith("/")) {
                name = name.substring(1)
            }
            if (name.startsWith("lib/")) {
                if (entry.isDirectory) {
                    continue
                }
                val sp = name.indexOf('/', 4)
                var en2add: String
                en2add = if (sp > 0) {
                    val osArch = name.substring(4, sp)
                    osArch.toLowerCase(Locale.getDefault())
                } else {
                    defaultArch
                }
                var ents = archLibEntries[en2add]
                if (ents == null) {
                    ents = LinkedList()
                    archLibEntries[en2add] = ents
                }
                ents.add(entry)
            }
        }
        val arch = System.getProperty("os.arch")
        var libEntries: List<ZipEntry>? = archLibEntries[arch!!.toLowerCase(Locale.getDefault())]
        if (libEntries == null) {
            libEntries = archLibEntries[defaultArch]
        }
        var hasLib = false
        if (libEntries != null) {
            hasLib = true
            tarDir.mkdirs()
            for (libEntry in libEntries) {
                val name = libEntry.name
                val pureName = name.substring(name.lastIndexOf('/') + 1)
                val target = File(tarDir, pureName)
                saveToFile(zip.getInputStream(libEntry), target)
            }
        }
        return hasLib
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun setAttrs(info: PluginInfo, manifestXML: String) {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(StringReader(manifestXML))
        var eventType = parser.eventType
        var namespaceAndroid: String? = null
        do {
            when (eventType) {
                XmlPullParser.START_DOCUMENT, XmlPullParser.END_TAG -> {
                }
                XmlPullParser.START_TAG -> {
                    val tag = parser.name
                    when {
                        tag == "manifest" -> {
                            namespaceAndroid = parser.getNamespace("android")
                        }
                        "activity" == parser.name -> {
                            addActivity(info, namespaceAndroid, parser)
                        }
                        "receiver" == parser.name -> {
                            addReceiver(info, namespaceAndroid, parser)
                        }
                        "service" == parser.name -> {
                            addService(info, namespaceAndroid, parser)
                        }
                        "application" == parser.name -> {
                            parseApplicationInfo(info, namespaceAndroid, parser)
                        }
                    }
                }
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseApplicationInfo(
        info: PluginInfo,
        namespace: String?,
        parser: XmlPullParser
    ) {
        val applicationName = parser.getAttributeValue(namespace, "name")
        val packageName = info.packageInfo.packageName
        val applicationInfo = info.packageInfo.applicationInfo
        applicationInfo.name = getName(applicationName, packageName)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun addActivity(
        info: PluginInfo, namespace: String?,
        parser: XmlPullParser
    ) {
        var eventType = parser.eventType
        var activityName = parser.getAttributeValue(namespace, "name")
        val packageName = info.packageInfo.packageName
        activityName = getName(activityName, packageName)
        val act = ResolveInfo()
        act.activityInfo = info.findActivityByClassNameFromPkg(activityName!!)
        do {
            if (eventType == XmlPullParser.START_TAG) {
                val tag = parser.name
                if ("intent-filter" == tag) {
                    if (act.filter == null) {
                        act.filter = IntentFilter()
                    }
                } else if ("action" == tag) {
                    val actionName = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    act.filter.addAction(actionName)
                } else if ("category" == tag) {
                    val category = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    act.filter.addCategory(category)
                } else if ("data" == tag) {
                    // TODO:
                }
            }
            eventType = parser.next()
        } while ("activity" != parser.name)
        info.addActivity(act)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun addService(
        info: PluginInfo, namespace: String?,
        parser: XmlPullParser
    ) {
        var eventType = parser.eventType
        var serviceName = parser.getAttributeValue(namespace, "name")
        val packageName = info.packageInfo.packageName
        serviceName = getName(serviceName, packageName)
        val service = ResolveInfo()
        service.serviceInfo = info.findServiceByClassName(serviceName!!)
        do {
            if (eventType == XmlPullParser.START_TAG) {
                val tag = parser.name
                if ("intent-filter" == tag) {
                    if (service.filter == null) {
                        service.filter = IntentFilter()
                    }
                } else if ("action" == tag) {
                    val actionName = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    service.filter.addAction(actionName)
                } else if ("category" == tag) {
                    val category = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    service.filter.addCategory(category)
                } else if ("data" == tag) {
                    // TODO:
                }
            }
            eventType = parser.next()
        } while ("service" != parser.name)
        //
        info.addService(service)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun addReceiver(
        info: PluginInfo, namespace: String?,
        parser: XmlPullParser
    ) {
        var eventType = parser.eventType
        var receiverName = parser.getAttributeValue(namespace, "name")
        val packageName = info.packageInfo.packageName
        receiverName = getName(receiverName, packageName)
        val receiver = ResolveInfo()
        // 此时的activityInfo 表示 receiverInfo
        receiver.activityInfo = info.findReceiverByClassName(receiverName!!)
        do {
            if (eventType == XmlPullParser.START_TAG) {
                val tag = parser.name
                if ("intent-filter" == tag) {
                    if (receiver.filter == null) {
                        receiver.filter = IntentFilter()
                    }
                } else if ("action" == tag) {
                    val actionName = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    receiver.filter.addAction(actionName)
                } else if ("category" == tag) {
                    val category = parser.getAttributeValue(
                        namespace,
                        "name"
                    )
                    receiver.filter.addCategory(category)
                } else if ("data" == tag) {
                    // TODO:
                }
            }
            eventType = parser.next()
        } while ("receiver" != parser.name)
        info.addReceiver(receiver)
    }

    private fun getName(nameOrig: String?, pkgName: String): String? {
        if (nameOrig == null) {
            return null
        }
        val sb: StringBuilder?
        if (nameOrig.startsWith(".")) {
            sb = StringBuilder()
            sb.append(pkgName)
            sb.append(nameOrig)
        } else if (!nameOrig.contains(".")) {
            sb = StringBuilder()
            sb.append(pkgName)
            sb.append('.')
            sb.append(nameOrig)
        } else {
            return nameOrig
        }
        return sb.toString()
    }
}