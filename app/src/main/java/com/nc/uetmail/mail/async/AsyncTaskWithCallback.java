package com.nc.uetmail.mail.async;

import android.os.AsyncTask;

public class AsyncTaskWithCallback {
    public interface CallbackInterface {
        void call();
    }

    public interface CallbackWithParamInterface<T> {
        void call(T param);
    }

    public static class AsyncCallback extends AsyncTask<Void, Void, Void> {
        private CallbackInterface callbackInterface;

        public AsyncCallback(final CallbackInterface callbackInterface) {
            this.callbackInterface = callbackInterface;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callbackInterface.call();
            return null;
        }
    }

}
