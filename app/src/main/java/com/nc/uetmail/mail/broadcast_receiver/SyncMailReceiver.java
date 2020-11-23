package com.nc.uetmail.mail.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nc.uetmail.mail.database.repository.MailRepository;

import java.util.Date;

public class SyncMailReceiver extends BroadcastReceiver {
    public static Date processDate = new Date();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (new Date().getTime() - SyncMailReceiver.processDate.getTime() > 300000 && isOnline(context)) {
            new MailRepository(context).syncMail();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            );
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //airplane mode => null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            return false;
        }
    }
}
