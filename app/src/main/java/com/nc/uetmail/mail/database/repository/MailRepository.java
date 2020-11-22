package com.nc.uetmail.mail.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.nc.uetmail.R;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.session.GMailHelper;
import com.nc.uetmail.mail.session.HelperCore;
import com.nc.uetmail.mail.session.MailHelper;
import com.nc.uetmail.mail.session.MailSession;

import java.util.Arrays;
import java.util.List;

public class MailRepository {
    private MailDatabase mailDatabase;
    private MailDao repo_dao;
    private Context context;
    private MutableLiveData<List<MailModel>> messagesInFolder = new MutableLiveData<>();

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

    public void deleteEmptyActiveUserMs() {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                UserModel activeInbUser = mailDatabase.userDao().getActiveInbUser();
                mailDatabase.folderDao().deleteByUid(activeInbUser.id);
                repo_dao.deleteByUid(activeInbUser.id);
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
            credential.setSelectedAccountName(activeInbUser.user);
            Gmail service = new Gmail.Builder(
                HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credential
            )
                .setApplicationName(context.getResources().getString(R.string.app_name))
                .build();
            return new GMailHelper(mailDatabase, service, activeInbUser);
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
                if (helperCore == null) return;
                try {
                    helperCore.listFolderAndMail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void sendMail(final MailModel mailModel) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                HelperCore helperCore = getMailHelper();
                if (helperCore == null) return;
                try {
                    UserDao userDao = mailDatabase.userDao();
                    UserModel activeInbUser = userDao.getActiveInbUser();
                    mailModel.mail_from = activeInbUser.email;
                    FolderModel folderModel = mailDatabase.folderDao().getByUidAndType(
                        activeInbUser.id, FolderModel.FolderType.INBOX.name()
                    );
                    helperCore.sendMail(folderModel, mailModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public LiveData<MailModel> getByMailId(int msid) {
        return repo_dao.getByMailId(msid);
    }

    public LiveData<List<MailModel>> getMails() {
        return repo_dao.getAll();
    }

    public LiveData<List<MailModel>> getLiveMailByActiveFolderId() {
        return repo_dao.getLiveMailByActiveFolderId();
    }

    public void getMailsInFolder(String type) {
        new QueryMailAsyncTask(this, mailDatabase, type).execute();
    }

    private void setMailsInFolder(List<MailModel> mails) {
        this.messagesInFolder.setValue(mails);
    }

    private static class QueryMailAsyncTask extends AsyncTask<String, Void, List<MailModel>> {
        private MailDatabase database;
        private MailRepository delegate;
        private String type;

        QueryMailAsyncTask(MailRepository delegate, MailDatabase database, String type) {
            this.delegate = delegate;
            this.database = database;
            this.type = type;
        }

        @Override
        protected List<MailModel> doInBackground(final String... params) {
            UserModel activeInbUser = database.userDao().getActiveInbUser();
            if (activeInbUser == null) activeInbUser = new UserModel();
            FolderModel fm = database.folderDao().getByUidAndType(activeInbUser.id, type);
            if (fm == null) fm = new FolderModel();
            return database.mailDao().getMailByFolderId(fm.id);
        }

        @Override
        protected void onPostExecute(List<MailModel> result) {
            delegate.setMailsInFolder(result);
        }
    }


}
