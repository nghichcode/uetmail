package com.nc.uetmail.main.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SyncMailReceiver extends BroadcastReceiver {
    public static boolean processing=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            final Context ctx = context;

            Thread thread=new Thread() {
                @Override
                public void run() {
                    SyncMailReceiver.processing = true;
                    Log.e("net.conn.nc", "Start TH");
                    try {
                        sleep(5000);
                        Log.e("net.conn.nc", "END TH");
                    } catch (InterruptedException e) {
                        Log.e("ncvn", e.toString());
                    } finally {
                        SyncMailReceiver.processing = false;
                    }
                }
            };
            if (!SyncMailReceiver.processing && isOnline(ctx)) {thread.start();}
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //airplane mode => null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            Log.e("ncvn", e.toString());
            return false;
        }
    }
}
