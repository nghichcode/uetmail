package com.nc.uetmail.mail.utils.interfaces;

import android.os.AsyncTask;

public class CallbackAsyncImpl extends AsyncTask<Void, Void, Void> {
    private CallbackInterface callbackInterface;

    public CallbackAsyncImpl(final CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        callbackInterface.callback();
        return null;
    }
}
