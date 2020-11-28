package com.hfax.ucard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hfax.app.utils.ToastUtils;
import com.hfax.lib.utils.Utils;
import com.hfax.ucard.bean.LoginBean;
import com.hfax.ucard.modules.entrance.UCardApplication;
import com.hfax.ucard.utils.mvp.NetworkAddress;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UCardUtil {
    private static String cut = " ";

    /**
     * 获取当前进程的进程名
     *
     * @return 进程名
     */
    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            FileUtils.closeCloseable(reader);
        }
        return null;
    }


    public static boolean isGoneDrainage() {
        return "3000000211".equals(Utils.CHANNEL);
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * 生成一个不重复的id
     *
     * @return
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1;
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static String getMd5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startActivity(final Context context, final Intent intent, final int requestCode, final Bundle bundle) {
        UCardApplication.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode, bundle);
                } else {
                    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), bundle);
                }
            }
        });

    }

    public static void startActivity(Context context, Intent intent, int requestCode) {
        startActivity(context, intent, requestCode, null);
    }

    public static void startActivity(Context context, Intent intent) {
        startActivity(context, intent, -1);
    }

    public static void setListViewHeightByItem(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View item = listAdapter.getView(i, null, listView);
            //item的布局要求是linearLayout，否则measure(0,0)会报错。
            item.measure(0, 0);
            //计算出所有item高度的总和
            totalHeight += item.getMeasuredHeight();
        }
        //获取ListView的LayoutParams,只需要修改高度就可以。
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //修改ListView高度为item总高度和所有分割线的高度总和。
        //这里的分隔线是指ListView自带的divider
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //将修改过的参数，重新设置给ListView
        listView.setLayoutParams(params);
    }

    public static String formatAmount(long cen) {
        return Utils.formatAmount("#####0.##", cen + "");
    }


    /**
     * 显示小数点后两位
     *
     * @param cen
     * @return
     */
    public static String formatAmount2(long cen) {
        return Utils.formatAmount("#####0.00", cen + "");
    }

    public static String formatDate(long cen) {

        return Utils.formatDate(cen * 1000, "yyyy.MM.dd");
    }

    public static String getBankCardLogo(String bankId, boolean isWhiteLogo) {
        if (isWhiteLogo) { //（白色Logo）使用页面：银行卡管理
            return getH5Url("/static/imgs/bank_logo_bg/bankico_" + bankId + "@2x.png");
        } else { //（彩色Logo）使用页面：提现、绑定银行卡、开通存管
            return getH5Url("/static/imgs/bank_logo/bankico_" + bankId + "@2x.png");
        }
    }

    public static String getAuthLogo(String logoId) {
        return getH5Url("/static/imgs/auth/auth_" + logoId + ".png");

    }

    public static boolean isCollectionEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static String getBankNumLast(String bankNum) {
        if (!TextUtils.isEmpty(bankNum) && bankNum.length() > 4) {
            return bankNum.substring(bankNum.length() - 4);
        }
        return "";
    }

    public static String getClassName(String name) {
        if (!TextUtils.isEmpty(name)) {
            int index = name.lastIndexOf(".");
            if (index > -1) {
                name = name.substring(index + 1);
            }
        }
        return name;
    }

    public static String getH5Url(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")) {
                return path;
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }
        return NetworkAddress.BASE_URL + "ucard-app/" + path;
    }


    /**
     * 开启应用详情
     *
     * @param activity
     */
    public static void startAppDetail(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(localIntent);
    }

    public static void showToast(Object obj, CharSequence msg) {
        ToastUtils.getToast().showToast(msg);
    }

    public static String getTime(long millisUntilFinished) {
        DecimalFormat decimalFormat = new DecimalFormat("#00");
        StringBuilder stringBuilder = new StringBuilder();
        millisUntilFinished = millisUntilFinished / 1000;
        long h = millisUntilFinished / 60 / 60;
        stringBuilder.append(decimalFormat.format(h));
        stringBuilder.append(":");
        long m = (millisUntilFinished / 60) % 60;
        stringBuilder.append(decimalFormat.format(m));
        stringBuilder.append(":");
        long s = millisUntilFinished % 60;
        stringBuilder.append(decimalFormat.format(s));
        return stringBuilder.toString();
    }

    public static boolean isEmpty(CharSequence Char) {
        return Char == null || Char.toString().trim().length() == 0;
    }

    public static String getLowAmount(long repay, long loanAmount, long periods) {
        if (TextUtils.equals(LoginBean.getUId(), "hfas140458176758857728")) {
            repay = (long) (loanAmount / periods + loanAmount * 0.01);
        }
        return formatAmount(repay);
    }

    public static String formatPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) return phone;
        return new StringBuilder(phone).insert(7, cut).insert(3, cut).toString();
    }

    public static String parsePhone(String phone) {
        if (isEmpty(phone)) return phone;
        return phone.replaceAll(cut, "");
    }

    public static String binStrToStr(String binStr) {
        String[] tempStr = binStr.split(" ");
        char[] tempChar = new char[tempStr.length];
        for (int i = 0; i < tempStr.length; i++) {
            tempChar[i] = binStrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }

    //将二进制字符串转换成int数组
    public static int[] binStrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    //将二进制转换成字符
    public static char binStrToChar(String binStr) {
        int[] temp = binStrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }
}
