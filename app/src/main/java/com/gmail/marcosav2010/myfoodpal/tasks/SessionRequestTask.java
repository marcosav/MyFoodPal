package com.gmail.marcosav2010.myfoodpal.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.IMFPSession;
import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfoodpal.common.Utils;
import com.gmail.marcosav2010.myfoodpal.storage.DataStorer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class SessionRequestTask extends AsyncTask<String, Void, SessionRequestResult> {

    private final WeakReference<Context> context;
    private final Consumer<SessionRequestResult> handler;

    public SessionRequestTask(Context context, Consumer<SessionRequestResult> handler) {
        this.context = new WeakReference<>(context);
        this.handler = handler;
    }

    public static SessionRequestResult execute(Context context, String user, String password) {
        if (!Utils.hasInternetConnection(context))
            return SessionRequestResult.from(SessionRequestResult.Type.NO_INTERNET_ERROR, null);
        try {
            IMFPSession session = MFPSession.create(user, password);
            return SessionRequestResult.from(SessionRequestResult.Type.SUCCESS, session);
        } catch (IOException ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.IO_ERROR, null);
        } catch (Exception ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.LOGIN_ERROR, null);
        }
    }

    public static void postExecute(Context context, SessionRequestResult got, Consumer<SessionRequestResult> handler) {
        DataStorer.load(context).setSession(got);
        handler.accept(got);
    }

    protected SessionRequestResult doInBackground(String... login) {
        return execute(context.get(), login[0], login[1]);
    }

    protected void onPostExecute(SessionRequestResult got) {
        postExecute(context.get(), got, handler);
    }
}