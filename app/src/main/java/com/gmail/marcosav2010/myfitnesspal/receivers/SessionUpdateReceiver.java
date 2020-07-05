package com.gmail.marcosav2010.myfitnesspal.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.marcosav2010.myfitnesspal.services.SessionRequestService;

import java.util.Objects;

public class SessionUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        android.os.Debug.waitForDebugger();
        Log.d("BOOT", Objects.requireNonNull(intent.getAction()));
        //if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED"))
        context.startForegroundService(new Intent(context, SessionRequestService.class));
    }
}