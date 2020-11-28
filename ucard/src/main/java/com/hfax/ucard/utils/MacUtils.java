package com.hfax.ucard.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.hfax.lib.BaseApplication;
import com.hfax.ucard.bean.ContactInfo;
import com.hfax.ucard.bean.InstallAppInfo;
import com.hfax.ucard.bean.Wifiinfo;
import com.hfax.ucard.bean.CurrentWifiInfo;
import com.hfax.ucard.utils.mvp.DataChange;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by liuweiping on 2018/7/3.
 */

public class MacUtils {
    public static String getMac(Context context) {

        String strMac;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            strMac = getLocalMacAddressFromWifiInfo(context);
            return strMac;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            strMac = getMacAddress(context);
            return strMac;
        } else {
            if (!TextUtils.isEmpty(getMacAddress())) {
                strMac = getMacAddress();
                return strMac;
            } else if (!TextUtils.isEmpty(getMachineHardwareAddress())) {
                Log.e("=====", "7.0以上2");
                strMac = getMachineHardwareAddress();
                return strMac;
            } else {
                strMac = getLocalMacAddressFromBusybox();
                return strMac;
            }
        }
    }

    /**
     * 根据wifi信息获取本地mac
     */
    private static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo winfo = wifi.getConnectionInfo();
            if (winfo != null) {
                return winfo.getMacAddress();
            }
        }
        return "";
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     */
    private static String getMacAddress(Context context) {

        // 如果是6.0以下，直接通过wifimanager获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String macAddress0 = getMacAddress0(context);
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0;
            }
        }
        String str = "";
        String macSerial = "";
        LineNumberReader input = null;
        InputStreamReader ir = null;
        InputStream inputStream = null;
        Process pp=null;
        try {
            pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            inputStream = pp.getInputStream();
            ir = new InputStreamReader(inputStream);
            input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            Log.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        } finally {
            FileUtils.closeCloseable(input);
            FileUtils.closeCloseable(ir);
            FileUtils.closeCloseable(inputStream);
            try {
                if(pp!=null){
                    pp.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (TextUtils.isEmpty(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("----->" + "NetInfoManager", "getMacAddress:" + e.toString());
            }

        }
        return macSerial;
    }

    private static String getMacAddress0(Context context) {
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;
            try {
                if (wifiMgr != null) {
                    wifiInfo = wifiMgr.getConnectionInfo();
                    if (wifiInfo != null) {
                        return wifiInfo.getMacAddress();
                    }
                }


            } catch (Exception e) {
                Log.e("----->" + "NetInfoManager", "getMacAddress0:" + e.toString());
            }

        }
        return "";

    }

    /**
     * Check whether accessing wifi state is permitted
     *
     */
    private static boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            Log.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:" + "access wifi state is enabled");
            return true;
        } else return false;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        try {
            return loadReaderAsString(reader);
        } finally {
            FileUtils.closeCloseable(reader);
        }

    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 根据IP地址获取MAC地址
     */
    private static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni =  en_netInterface.nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().contains(":")) break;
                    else ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

//    /**
//     * 获取本地IP
//     *
//     */
//    private static String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
    /**
     * 获取设备HardwareAddress地址
     *
     */
    private static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     *
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    /**
     * 根据busybox获取本地Mac
     */
    private static String getLocalMacAddressFromBusybox() {
        String result;
        String Mac;
        result = callCmd("busybox ifconfig", "HWaddr");
        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            result = Mac;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {
        StringBuilder result = new StringBuilder();
        String line;
        InputStreamReader is = null;
        BufferedReader br = null;
        InputStream inputStream = null;
        Process proc=null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            inputStream = proc.getInputStream();
            is = new InputStreamReader(inputStream);
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null && !line.contains(filter)) {
                result.append(line);
            }
            result = new StringBuilder(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeCloseable(is);
            FileUtils.closeCloseable(br);
            FileUtils.closeCloseable(inputStream);
            try {
                if(proc!=null){
                    proc.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    static List<InstallAppInfo> getAppList() {
        ArrayList<InstallAppInfo> list = new ArrayList<>();
        try {
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            List<PackageInfo> infoList = pm.getInstalledPackages(0);
            if (!UCardUtil.isCollectionEmpty(infoList)) {
                for (PackageInfo packinfo : infoList) {
                    //过滤掉系统app
                    if ((ApplicationInfo.FLAG_SYSTEM & packinfo.applicationInfo.flags) != 0) {
                        continue;
                    }
                    String packageName = packinfo.packageName;
                    String appname = packinfo.applicationInfo.loadLabel(pm).toString();
                    InstallAppInfo ai = new InstallAppInfo();
                    ai.name = appname;
                    ai.packageName = packageName;
                    list.add(ai);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取wifi信息
     */
    public static CurrentWifiInfo getWifi() {
        CurrentWifiInfo wifiInfo = new CurrentWifiInfo();
        Wifiinfo thisWifi = getThisWifi();
        wifiInfo.wifiList = getWifiList();
        if (thisWifi != null) {
            wifiInfo.currentSsid = thisWifi.ssid;
            wifiInfo.currentBssid = thisWifi.bssid;
        }
        return wifiInfo;
    }

    private static List<Wifiinfo> getWifiList() {
        ArrayList<Wifiinfo> list = new ArrayList<>();
        try {
            WifiManager wifiManager = (WifiManager) BaseApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (!UCardUtil.isCollectionEmpty(scanResults)) {
                    for (ScanResult scanResult : scanResults) {
                        Wifiinfo info = new Wifiinfo();
                        info.ssid = scanResult.SSID;
                        info.bssid = scanResult.BSSID;
                        list.add(info);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Wifiinfo getThisWifi() {
        WifiManager wifiManager = (WifiManager) BaseApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            Wifiinfo info = new Wifiinfo();
            info.ssid = connectionInfo.getSSID().replace("\"", "");
            info.bssid = connectionInfo.getBSSID();
            return info;
        }
        return null;
    }

    public static void getContacts(final DataChange<List<ContactInfo>> dataChange) {
        Observable.create(new Observable.OnSubscribe<List<ContactInfo>>() {
            @Override
            public void call(Subscriber<? super List<ContactInfo>> subscriber) {
                subscriber.onNext(getContactInfo());
            }
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<ContactInfo>>() {
            @Override
            public void call(List<ContactInfo> o) {
                dataChange.onChange(o);
            }
        });
    }

    /**
     * 读取手机里面的联系人
     */

    private static List<ContactInfo> getContactInfo() {
        Cursor cursor = null;
        try {
            //获取联系人信息的Uri
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            //获取ContentResolver
            ContentResolver contentResolver = BaseApplication.getContext().getContentResolver();
            //查询数据，返回Cursor
            cursor = contentResolver.query(uri, null, null, null, null);
            List<ContactInfo> list = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                ContactInfo info = new ContactInfo();
                //获取联系人的ID
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //获取联系人的姓名
                info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));//联系人ID

                //查询电话类型的数据操作
                Cursor phones = null;
                try {
                    phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones != null && phones.moveToNext()) {
                        info.mobile += phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + ";";
                    }
                    if (!TextUtils.isEmpty(info.mobile)) {
                        info.mobile = info.mobile.substring(0, info.mobile.length() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtils.closeCloseable(phones);
                }


                //查询Email类型的数据操作
                Cursor emails = null;
                try {
                    emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                    if (emails != null && emails.moveToNext()) {
                        //添加Email的信息
                        info.email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtils.closeCloseable(emails);
                }

                //查询==地址==类型的数据操作.StructuredPostal.TYPE_WORK
                Cursor address = null;
                try {
                    address = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId, null, null);
                    if (address != null && address.moveToNext()) {
                        info.address = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtils.closeCloseable(address);
                }
                //Log.i("=========ddddddddddd=====", sb.toString());

                //查询==公司名字==类型的数据操作.Organization.COMPANY  ContactsContract.Data.CONTENT_URI
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                Cursor orgCur = null;

                try {
                    orgCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, orgWhere, orgWhereParams, null);
                    if (orgCur != null) {
                        if (orgCur.moveToFirst()) {
                            //组织名 (公司名字)
                            info.company = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                            //职位
                            info.job = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FileUtils.closeCloseable(orgCur);
                }
                list.add(info);
            }

            return list;
        } catch (Exception e) {
            return null;
        } finally {
            FileUtils.closeCloseable(cursor);
        }
    }

}
