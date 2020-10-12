package com.nc.uetmail.mail.async;

import android.content.Context;
import android.os.AsyncTask;

import com.nc.uetmail.mail.database.MailDatabase;

public class InitDB extends AsyncTask<Void, Void, Void> {
    Context ctx;

    public InitDB(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MailDatabase.getInstance(ctx);
        return null;
    }
}
