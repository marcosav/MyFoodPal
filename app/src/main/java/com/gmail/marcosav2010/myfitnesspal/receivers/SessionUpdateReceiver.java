package com.gmail.marcosav2010.myfitnesspal.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.marcosav2010.myfitnesspal.services.SessionRequestService;

public class SessionUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startForegroundService(new Intent(context, SessionRequestService.class));
    }
}