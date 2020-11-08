package com.nc.uetmail.mail.async;

import android.content.Context;
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

    public static class AsyncCallbackParam<T> extends AsyncTask<Void, Void, Void> {
        private CallbackWithParamInterface<T> running;
        private CallbackWithParamInterface<T> postRun;
        private T param;

        public AsyncCallbackParam(final T param, final CallbackWithParamInterface<T> running) {
            this.param = param;
            this.running = running;
        }

        public AsyncCallbackParam(final T param,
                                  final CallbackWithParamInterface<T> running,
                                  final CallbackWithParamInterface<T> postRun) {
            this.param = param;
            this.running = running;
            this.postRun = postRun;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (running != null) running.call(param);
            running.call(param);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (postRun != null) postRun.call(param);
        }
    }

}
