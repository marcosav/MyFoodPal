package com.gmail.marcosav2010.myfitnesspal.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gmail.marcosav2010.myfitnesspal.common.Utils;
import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;
import com.gmail.marcosav2010.myfitnesspal.logic.config.PreferenceManager;
import com.gmail.marcosav2010.myfitnesspal.logic.food.MFPSessionRequestResult;
import com.gmail.marcosav2010.myfitnesspal.logic.food.SessionRequestTask;
import com.gmail.marcosav2010.myfitnesspal.receivers.SessionUpdateReceiver;

public class SessionRequestService extends Service {

    private static final int DELAY = 120 * 1000;
    private static final int RETRY_DELAY = 10 * 1000;
    private static final int REQUEST_CODE = 98356922;

    private DataStorer dataStorer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendRequest();
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendRequest() {
        try {
            dataStorer = DataStorer.load(getApplicationContext());

            PreferenceManager preferenceManager = dataStorer.getPreferenceManager();

            String user = preferenceManager.getMFPUsername(), password = preferenceManager.getMFPPassword();
            if (user != null && password != null)
                new SessionRequestTask(getApplicationContext(), this::handleResult).execute(user, password);

        } catch (Exception ex) {
            Utils.sendNotification(getApplicationContext(), 2, "Error on send", ex + " " + ex.getMessage());
        }
    }

    private void handleResult(MFPSessionRequestResult r) {
        try {
            switch (r.getType()) {
                case SUCCESS:
                case NO_INTERNET_ERROR:
                    repeat(DELAY);
                    break;
                case IO_ERROR:
                    repeat(RETRY_DELAY);
            }
        } catch (Exception ex) {
            Utils.sendNotification(getApplicationContext(), 3, "Error on handle", ex + " " + ex.getMessage());
        }
    }

    private void repeat(long wait) {
        Intent intent = new Intent(this, SessionUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null)
            return;
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + wait, pendingIntent);

        //Utils.sendNotification(getApplicationContext(), 4, "Repeating in " + (wait / 1000) + " seconds", new Date().toString());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
