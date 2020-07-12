package com.gmail.marcosav2010.myfoodpal.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.marcosav2010.myfoodpal.services.SessionService;

public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            android.os.Debug.waitForDebugger();
        }*/
        SessionService.enqueueWork(context, new Intent());
    }
}