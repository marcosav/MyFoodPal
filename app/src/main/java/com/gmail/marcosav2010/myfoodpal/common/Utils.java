package com.gmail.marcosav2010.myfoodpal.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;

import com.gmail.marcosav2010.myfoodpal.R;

public class Utils {

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cManager.getActiveNetwork() != null;
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