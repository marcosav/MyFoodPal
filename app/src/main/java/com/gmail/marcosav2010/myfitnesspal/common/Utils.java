package com.gmail.marcosav2010.myfitnesspal.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;

import com.gmail.marcosav2010.myfitnesspal.R;

public class Utils {

    private static final String CHANNEL_NAME = "mav";
    private static final String CHANNEL_ID = "com.gmail.marcosav2010.channel.mav";

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cManager.getActiveNetwork() != null;
    }

    public static void sendNotification(Context context, int id, String title, String body) {
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        notificationChannel.setShowBadge(true);
        /*notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.YELLOW);*/

        if (notificationManager == null)
            return;
        notificationManager.createNotificationChannel(notificationChannel);

        Notification n = getNotification(context, title, body).build();

        notificationManager.notify(id, n);
    }

    private static Notification.Builder getNotification(Context context, String title, String body) {
        return new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_reload)
                .setAutoCancel(true);
    }
}
