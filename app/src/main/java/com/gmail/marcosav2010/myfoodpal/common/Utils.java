package com.gmail.marcosav2010.myfoodpal.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;

import com.gmail.marcosav2010.myfoodpal.R;

public class Utils {

    private static final String CHANNEL_NAME = "mav";
    private static final String CHANNEL_ID = "com.gmail.marcosav2010.myfoodpal.channel.mav";

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

    public static Integer copyToClipboard(Activity activity, String content) {
        try {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied List", content);
            clipboard.setPrimaryClip(clip);

            return R.string.successfully_copied;
        } catch (Exception ex) {
            return R.string.error_copy;
        }
    }

    public static Integer shareWhatsApp(Activity activity, String content) {
        Intent wIntent = new Intent(Intent.ACTION_SEND);

        wIntent.setType("text/plain");
        wIntent.setPackage("com.whatsapp");
        wIntent.putExtra(Intent.EXTRA_TEXT, content);

        try {
            activity.startActivity(wIntent);
        } catch (Exception ex) {
            return R.string.error_whatsapp;
        }

        return null;
    }

    public static int darker(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }
}