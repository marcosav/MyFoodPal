package com.gmail.marcosav2010.myfoodpal.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.gmail.marcosav2010.myfoodpal.receivers.BootReceiver;
import com.gmail.marcosav2010.myfoodpal.storage.SessionStorage;
import com.gmail.marcosav2010.myfoodpal.storage.PreferenceManager;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestResult;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestTask;

public class SessionService extends JobIntentService {

    private static final int DELAY = 3 * 3600 * 1000 / 2;
    private static final int RETRY_DELAY = 30 * 1000;

    private static final int REQUEST_CODE = 98356921;
    private static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, SessionService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        sendRequest();
    }

    private void sendRequest() {
        try {
            PreferenceManager preferenceManager = PreferenceManager.load(getApplicationContext());
            String user = preferenceManager.getMFPUsername(),
                    password = preferenceManager.getMFPPassword();

            if (user != null && password != null) {
                SessionRequestResult res = SessionRequestTask
                        .execute(getApplicationContext(), user, password);
                SessionRequestTask.postExecute(getApplicationContext(), res, this::handleResult);
            }

        } catch (Exception ex) {
            /*Log.d("TEST", ex.getMessage(), ex);
            Utils.sendNotification(getApplicationContext(), 2, "Error on send", ex + " " + ex.getMessage());*/
        }
    }

    private void handleResult(SessionRequestResult r) {
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
            /*Log.d("TEST", ex.getMessage(), ex);
            Utils.sendNotification(getApplicationContext(), 3, "Error on handle", ex + " " + ex.getMessage());*/
        }
    }

    private void repeat(long wait) {
        Intent intent = new Intent(this, BootReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null)
            return;
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + wait, pendingIntent);
    }
}
