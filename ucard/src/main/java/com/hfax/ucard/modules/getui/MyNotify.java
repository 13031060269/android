package com.hfax.ucard.modules.getui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.hfax.lib.utils.GsonUtils;
import com.hfax.ucard.R;
import com.hfax.ucard.modules.entrance.StartActivity;
import com.hfax.ucard.modules.home.MainActivity;

/**
 * Created by liuweiping on 2019/2/14.
 */

public class MyNotify {
    static int id;

    /**
     * 显示通知信息
     */
    public static void showNotification(Context context, PushMessageBean pushMessage) {
        if (pushMessage != null) {
            id++;
            String title = (!TextUtils.isEmpty(pushMessage.getTitle())) ? pushMessage.getTitle() : "惠域U卡";
            String content = (!TextUtils.isEmpty(pushMessage.getBody())) ? pushMessage.getBody() : "";
            Intent intent = new Intent(context, MainActivity.class); // 启动栈顶的activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.putExtra(MainActivity.KEY_PAGE, pushMessage.getPHPushNotificationKeyTabIndex());
            if (!TextUtils.isEmpty(pushMessage.getPHPushNotificationKeyUrl())) {
                Uri uri = Uri.parse(pushMessage.getPHPushNotificationKeyUrl());
                intent.setData(uri);
            }
            PendingIntent activity = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) return;
            final NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(String.valueOf(id), "channel_ucard", NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mBuilder = new NotificationCompat.Builder(context, String.valueOf(id));
                notificationManager.createNotificationChannel(mChannel);
                mBuilder.setChannelId(String.valueOf(id));
            } else {
                mBuilder = new NotificationCompat.Builder(context, null);
            }
            mBuilder.setContentTitle(title).setContentText(content).setPriority(Notification.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(activity).setSmallIcon(R.mipmap.pushsmall).setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.app_logo)).setAutoCancel(true);
            notificationManager.notify(id, mBuilder.build());
        }
    }
}
