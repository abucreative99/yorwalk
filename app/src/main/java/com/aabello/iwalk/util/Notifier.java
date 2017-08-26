package com.aabello.iwalk.util;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class Notifier {

    private static NotificationManager mNotificationManager;

    public static void notify(int notificationId, Context context, int smallIcon,
                              Bitmap largeIcon, String title, String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setSmallIcon(smallIcon).setColor(Color.parseColor("#073B3A"))
                        .setLargeIcon(largeIcon).setContentTitle(title).setContentText(text);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

}
