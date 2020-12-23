package com.gmail.marcosav2010.myfoodpal.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestResult;

import lombok.Getter;
import lombok.NonNull;

public class DataStorer {

    private static final String PREFERENCES_NAME = "global_preferences";
    private static final String MFP_SESSION = "mfp_session";
    private static final String MFP_LOGIN_DATE = "mfp_login_date";
    private static final String MFP_LOGIN_RESULT = "mfp_login_result";

    private static DataStorer instance;

    private IMFPSession session;

    @Getter
    private PreferenceManager preferenceManager;

    private SharedPreferences preferences;

    private DataStorer(Context c) {
        preferences = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferenceManager = new PreferenceManager(preferences);
    }

    public static DataStorer load(@NonNull Context c) {
        if (instance == null)
            instance = new DataStorer(c);
        return instance;
    }

    public IMFPSession getSession() {
        IMFPSession s;
        if (session == null) {
            String savedSession = getSavedSession();
            if (savedSession != null) {
                s = MFPSession.from(savedSession);
                if (!s.shouldReLog())
                    session = s;
            }
        } else if (session.shouldReLog())
            session = null;

        return session;
    }

    public void setSession(SessionRequestResult result) {
        this.session = result.getResult();
        saveSession(result);
    }

    private void saveSession(SessionRequestResult result) {
        preferences.edit()
                .putString(MFP_SESSION, session == null ? null : session.encode())
                .putLong(MFP_LOGIN_DATE, System.currentTimeMillis())
                .putString(MFP_LOGIN_RESULT, result.getType().name())
                .apply();
    }

    private String getSavedSession() {
        return preferences.getString(MFP_SESSION, null);
    }

    public long getLoginDate() {
        return preferences.getLong(MFP_LOGIN_DATE, 0L);
    }

    public String getLoginResult() {
        return preferences.getString(MFP_LOGIN_RESULT, null);
    }
}
