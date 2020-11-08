package com.nc.uetmail.mail.database.repository;

import androidx.lifecycle.LiveData;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.nc.uetmail.R;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.session.GMailHelper;
import com.nc.uetmail.mail.session.HelperCore;
import com.nc.uetmail.mail.session.MailHelper;
import com.nc.uetmail.mail.session.MailSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MailRepository {
    private MailDatabase mailDatabase;
    private MailDao repo_dao;
    private Context context;

    private static final List<String> SCOPES = Arrays.asList(new String[]{
        GmailScopes.MAIL_GOOGLE_COM,
        GmailScopes.GMAIL_ADDONS_CURRENT_ACTION_COMPOSE,
        GmailScopes.GMAIL_ADDONS_CURRENT_MESSAGE_ACTION,
        GmailScopes.GMAIL_ADDONS_CURRENT_MESSAGE_METADATA,
        GmailScopes.GMAIL_ADDONS_CURRENT_MESSAGE_READONLY,
        GmailScopes.GMAIL_MODIFY,
        GmailScopes.GMAIL_READONLY,
        GmailScopes.GMAIL_COMPOSE,
        GmailScopes.GMAIL_INSERT,
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_SEND,
        GmailScopes.GMAIL_SETTINGS_BASIC,
        GmailScopes.GMAIL_SETTINGS_SHARING,
    });

    public MailRepository(Context context) {
        mailDatabase = MailDatabase.getInstance(context);
        repo_dao = mailDatabase.mailDao();
        this.context = context;
    }

    public void insert(final MailModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.insert(model);
            }
        }).execute();
    }

    public void update(final MailModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.update(model);
            }
        }).execute();
    }

    public void delete(final MailModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.delete(model);
            }
        }).execute();
    }

    private HelperCore getMailHelper() {
        MailMasterDao masterDao = mailDatabase.mailMasterDao();
        UserDao userDao = mailDatabase.userDao();
        UserModel activeInbUser = userDao.getActiveInbUser();
        UserModel activeOubUser = userDao.getActiveOubUser();
        if (activeInbUser == null || activeOubUser == null) return null;
        if (UserModel.MailProtocol.GMAIL.eq(activeInbUser.protocol)) {
            HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
            GoogleAccountCredential credential = GoogleAccountCredential
                .usingOAuth2(context, SCOPES).setBackOff(new ExponentialBackOff());
            Gmail service = new Gmail.Builder(
                HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credential
            )
                .setApplicationName(context.getResources().getString(R.string.app_name))
                .build();
            return new GMailHelper(mailDatabase, service);
        } else {
            UserModel inb = UserRepository.decryptUser(masterDao, activeInbUser);
            UserModel oub = UserRepository.decryptUser(masterDao, activeOubUser);
            try {
                MailSession ms = MailSession.getInstance(inb, oub);
                return new MailHelper(mailDatabase, ms);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void syncMail() {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                HelperCore helperCore = getMailHelper();
                if (helperCore==null) return;
                try {
                    helperCore.listFolderAndMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public LiveData<MailModel> getByMessageId(int msid) {
        return repo_dao.getByMessageId(msid);
    }

    public LiveData<List<MailModel>> getMessages() {
        return repo_dao.getAll();
    }

}
