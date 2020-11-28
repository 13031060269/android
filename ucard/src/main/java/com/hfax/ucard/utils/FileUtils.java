package com.hfax.ucard.utils;

import com.hfax.lib.BaseApplication;
import com.hfax.lib.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.ZipFile;

import okhttp3.ResponseBody;

/**
 * Created by liuweiping on 2018/5/17.
 */

public class FileUtils {
    private static String DOWNLOAD_FILE = "download";//下载的文件存放的位置
    private static String RN_BUNDLE = "rnBundle";//rn文件的文件夹
    private static String BUNDLE_FILE = "uCard.android.bundle";//rn文件名

    public static void writeToFile(InputStream dataIns, File target) throws IOException {
        FileOutputStream fo = null;
        try {
            byte[] buff = new byte[1024];
            int b;
            fo = new FileOutputStream(target);
            while ((b = dataIns.read(buff)) != -1) {
                fo.write(buff, 0, b);
            }
        } finally {
            closeCloseable(fo);
            closeCloseable(dataIns);
        }
    }

    public static void writeToFile(byte[] data, File target) throws IOException {
        writeToFile(new ByteArrayInputStream(data), target);
    }

    public static void copyFile(File source, File target) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeCloseable(fi);
            closeCloseable(in);
            closeCloseable(fo);
            closeCloseable(out);
        }
    }

    public static void closeCloseable(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取下载的路径
     *
     * @param url
     * @return
     */
    public static File getDownloadFile(String url) {
        File downloadFile = new File(getCacheDir(), DOWNLOAD_FILE);
        if (!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        if (url == null) {
            return downloadFile;
        }
        return new File(downloadFile, Utils.md5Encode(url));
    }

    public static String getFileMd5(File file) {
        StringBuffer buf = new StringBuffer();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int b = -1;
            byte[] buff = new byte[1024];
            while ((b = fileInputStream.read(buff)) != -1) {
                md5.update(buff, 0, b);
            }
            byte[] bytes = md5.digest();
            for (int i = 0; i < bytes.length; ++i) {
                String s = Integer.toHexString(bytes[i] & 255);
                if (s.length() == 1) {
                    buf.append("0");
                }
                buf.append(s);
            }
        } catch (Exception var6) {
        }finally {
            closeCloseable(fileInputStream);
        }
        return buf.toString();
    }

    /**
     * 下载文件到本地
     *
     * @param body
     * @param fileOut
     * @param append
     * @throws IOException
     */
    public static void loadFile(ResponseBody body, File fileOut, boolean append) throws IOException {
        FileOutputStream fo = null;
        InputStream inputStream = null;
        try {
            inputStream = body.byteStream();
            fo = new FileOutputStream(fileOut, append);
            int len = -1;
            byte[] buff = new byte[8 * 1024];
            while ((len = inputStream.read(buff)) != -1) {
                fo.write(buff, 0, len);
            }
        } finally {
            closeCloseable(fo);
            closeCloseable(inputStream);
        }
    }

    /**
     * 得到rn的bundle文件的路径
     *
     * @return
     */
    public static String getBundlePath() {
        return getBundleFile().getAbsolutePath();
    }

    /**
     * 初始化bundle文件，如果有最新的则复制最新的，如果文件不存在就从asset解压
     */
    public static void initBundleFile() throws Exception {
        File bundleFile = getBundleFile();
        File cacheFile = getCacheFile();
        if (cacheFile.exists()) {
            copyFile(cacheFile, bundleFile);
            cacheFile.delete();
        }
        if (!bundleFile.exists()) {
            String assetName = BUNDLE_FILE + ".zip";
            InputStream open = BaseApplication.getContext().getAssets().open(assetName);
            File zipSource = new File(bundleFile.getParentFile(), assetName);
            writeToFile(open, zipSource);
            ZipFile zip = null;
            try {
                zip = new ZipFile(zipSource);
                InputStream inputStream = zip.getInputStream(zip.getEntry(BUNDLE_FILE));
                writeToFile(inputStream, bundleFile);
            } finally {
                if (zip != null) {
                    try {
                        zip.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void clearCache() {
        File bundleFile = getBundleFile();
        if (bundleFile.exists()) bundleFile.delete();
        File cacheFile = getCacheFile();
        if (cacheFile.exists()) cacheFile.delete();
    }

    /**
     * 得到rn的bundle文件
     *
     * @return
     */
    public static File getBundleFile() {
        File bundleRoot = new File(getCacheDir(), RN_BUNDLE);
        if (!bundleRoot.exists()) {
            bundleRoot.mkdirs();
        }
        return new File(bundleRoot, BUNDLE_FILE);
    }

    private static File getCacheDir() {
        return BaseApplication.getContext().getCacheDir();
    }

    public static void unZipBundle(File zipSource) {
        ZipFile zip = null;
        try {
            File bundleFile = getCacheFile();
            zip = new ZipFile(zipSource);
            InputStream inputStream = zip.getInputStream(zip.getEntry(BUNDLE_FILE));
            writeToFile(inputStream, bundleFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取网络缓存的bundle文件
     *
     * @return
     */
    private static File getCacheFile() {
        return new File(getDownloadFile(null), BUNDLE_FILE);
    }
}
