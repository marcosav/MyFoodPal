package com.gmail.marcosav2010.myfoodpal.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.marcosav2010.json.JSONArray;
import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfoodpal.tasks.SessionRequestResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.NonNull;

public class SessionStorage {

    public static final int MAX_MEALS = 6;

    private static final String MFP_SESSION = "mfp_session";
    private static final String MFP_LOGIN_DATE = "mfp_login_date";
    private static final String MFP_LOGIN_RESULT = "mfp_login_result";
    private static final String MFP_MEALS = "mfp_meals";

    private static final List<String> DEFAULT_MEALS = IntStream.range(0, MAX_MEALS)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());

    private static SessionStorage instance;

    private IMFPSession session;

    private final SharedPreferences preferences;

    private SessionStorage(Context c) {
        preferences = ApplicationPreferences.load(c);
    }

    public static SessionStorage load(@NonNull Context c) {
        if (instance == null)
            instance = new SessionStorage(c);
        return instance;
    }

    public IMFPSession getSession() {
        IMFPSession s;
        if (session == null) {
            String savedSession = getSavedSession();
            if (savedSession != null) {
                s = MFPSession.from(savedSession, null);
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
        SharedPreferences.Editor e = preferences.edit()
                .putString(MFP_SESSION, session == null ? null : session.encode())
                .putString(MFP_LOGIN_RESULT, result.getType().name());

        if (session != null) {
            e.putLong(MFP_LOGIN_DATE, session.getCreationTime())
                    .putString(MFP_MEALS, new JSONArray(session.toUser().getMealNames()).toString());
        }

        e.apply();
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

    public List<String> getMeals() {
        String raw = preferences.getString(MFP_MEALS, null);
        return raw == null ? DEFAULT_MEALS : new JSONArray(raw).toList().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
