package com.gmail.marcosav2010.myfoodpal.tasks;

import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.LoginException;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfoodpal.model.settings.Cookies;

import java.util.function.Consumer;

public class SessionRequestTask extends AsyncTask<Cookies, Void, SessionRequestResult> {

    private final boolean hasInternet;
    private final Consumer<SessionRequestResult> handler;

    public SessionRequestTask(boolean hasInternet, Consumer<SessionRequestResult> handler) {
        this.hasInternet = hasInternet;
        this.handler = handler;
    }

    public static SessionRequestResult execute(boolean hasInternet, Cookies cookies) {
        if (!hasInternet)
            return SessionRequestResult.from(SessionRequestResult.Type.NO_INTERNET_ERROR, null);
        try {
            IMFPSession session = MFPSession.create("a", "a", new PreloadedLoginHandler(cookies));
            return SessionRequestResult.from(SessionRequestResult.Type.SUCCESS, session);
        } catch (LoginException ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.LOGIN_ERROR, null);
        } catch (Exception ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.IO_ERROR, null);
        }
    }

    protected SessionRequestResult doInBackground(Cookies... cookies) {
        return execute(hasInternet, cookies[0]);
    }

    protected void onPostExecute(SessionRequestResult got) {
        handler.accept(got);
    }
}