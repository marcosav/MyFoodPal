package com.gmail.marcosav2010.myfitnesspal.logic.food;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.marcosav2010.myfitnesspal.api.MFPSession;
import com.gmail.marcosav2010.myfitnesspal.common.Utils;
import com.gmail.marcosav2010.myfitnesspal.logic.DataStorer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class SessionRequestTask extends AsyncTask<String, Void, MFPSessionRequestResult> {

    private final WeakReference<Context> context;
    private final Consumer<MFPSessionRequestResult> handler;

    public SessionRequestTask(Context context, Consumer<MFPSessionRequestResult> handler) {
        this.context = new WeakReference<>(context);
        this.handler = handler;
    }

    protected MFPSessionRequestResult doInBackground(String... login) {
        if (!Utils.hasInternetConnection(context.get()))
            return MFPSessionRequestResult.from(MFPSessionRequestResult.Type.NO_INTERNET_ERROR, null);

        try {
            return MFPSessionRequestResult.from(MFPSessionRequestResult.Type.SUCCESS, MFPSession.create(login[0], login[1]));
        } catch (IOException ex) {
            return MFPSessionRequestResult.from(MFPSessionRequestResult.Type.IO_ERROR, null);
        } catch (Exception ex) {
            return MFPSessionRequestResult.from(MFPSessionRequestResult.Type.LOGIN_ERROR, null);
        }
    }

    protected void onPostExecute(MFPSessionRequestResult got) {
        DataStorer.load(context.get()).setSession(got);
        handler.accept(got);
    }
}