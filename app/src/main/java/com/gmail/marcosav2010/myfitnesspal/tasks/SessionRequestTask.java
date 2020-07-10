package com.gmail.marcosav2010.myfitnesspal.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.common.Utils;
import com.gmail.marcosav2010.myfitnesspal.storage.DataStorer;

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

    protected SessionRequestResult doInBackground(String... login) {
        if (!Utils.hasInternetConnection(context.get()))
            return SessionRequestResult.from(SessionRequestResult.Type.NO_INTERNET_ERROR, null);

        try {
            return SessionRequestResult.from(SessionRequestResult.Type.SUCCESS, MFPSession.create(login[0], login[1]));
        } catch (IOException ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.IO_ERROR, null);
        } catch (Exception ex) {
            return SessionRequestResult.from(SessionRequestResult.Type.LOGIN_ERROR, null);
        }
    }

    protected void onPostExecute(SessionRequestResult got) {
        DataStorer.load(context.get()).setSession(got);
        handler.accept(got);
    }
}