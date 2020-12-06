package com.nc.uetmail.mail.database.repository;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

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
import com.nc.uetmail.mail.utils.MailAndroidUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MailRepository {
    public MailDatabase mailDatabase;
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

    private HelperCore getMailHelper() throws Exception {
        MailMasterDao masterDao = mailDatabase.mailMasterDao();
        UserDao userDao = mailDatabase.userDao();
        UserModel activeInbUser = userDao.getActiveInbUser();
        UserModel activeOubUser = userDao.getActiveOubUser();
        if (activeInbUser == null || activeOubUser == null) {
            throw new Exception("");
        }
        activeInbUser.updateTime();
        userDao.update(activeInbUser);
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
            return new GMailHelper(context, mailDatabase, service, activeInbUser);
        } else {
            UserModel inb = UserRepository.decryptUser(masterDao, activeInbUser);
            UserModel oub = UserRepository.decryptUser(masterDao, activeOubUser);
            try {
                MailSession ms = MailSession.getInstance(inb, oub);
                return new MailHelper(context, mailDatabase, ms);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e.toString());
            }
        }
    }

    public void syncMail() {
        syncMail(false);
    }

    public void syncMail(boolean force) {
        String appId = context.getString(R.string.app_id);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.getNotificationChannel(appId) == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager.getNotificationChannel(appId) == null) {
                    NotificationChannel channel = new NotificationChannel(
                        appId,
                        context.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                    );
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }
        notificationManager.notify(MailAndroidUtils.NOTIFICATION_ID,
            new NotificationCompat.Builder(context, appId)
                .setSmallIcon(R.mipmap.mail_icon)
                .setContentTitle(context.getString(R.string.mail_title_info))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.mail_sync_start)))
//                .setContentText(context.getString(R.string.mail_sync_start))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .build()
        );
        new SyncMailTask(context, force, this).execute();
    }

    private static class SyncMailTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private boolean force;
        private MailRepository repository;

        SyncMailTask(Context context, boolean force, MailRepository repository) {
            this.context = context;
            this.force = force;
            this.repository = repository;
        }

        @Override
        protected String doInBackground(final Void... params) {
            try {
                UserModel activeInbUser = repository.mailDatabase.userDao().getActiveInbUser();
                if (activeInbUser == null) return context.getString(R.string.mail_flag_clear);
                if (!force && activeInbUser.updated_at != null
                    && new Date().getTime() - activeInbUser.updated_at.getTime() > 300000
                )
                    return context.getString(R.string.mail_flag_clear);
                HelperCore helperCore = repository.getMailHelper();
                if (helperCore == null) return context.getString(R.string.mail_flag_clear);
                helperCore.listFolderAndMail();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            boolean hasError = result != null && !result.trim().isEmpty();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (context.getString(R.string.mail_flag_clear).equals(result)) {
                notificationManager.cancel(MailAndroidUtils.NOTIFICATION_ID);
                return;
            }
            notificationManager.notify(MailAndroidUtils.NOTIFICATION_ID,
                new NotificationCompat.Builder(context, context.getString(R.string.app_id))
                    .setSmallIcon(R.mipmap.mail_icon)
                    .setContentTitle(context.getString(
                        hasError ? R.string.mail_title_danger : R.string.mail_title_info
                    ))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        hasError ? result : context.getString(R.string.mail_sync_finish)
                    ))
//                    .setContentText(
//                        hasError ? result : context.getString(R.string.mail_sync_finish)
//                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
            );
        }
    }

    public void seenMail(final MailModel mailModel) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                try {
                    HelperCore helperCore = getMailHelper();
                    if (helperCore == null) return;
                    helperCore.seenMail(mailModel);
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
                try {
                    HelperCore helperCore = getMailHelper();
                    if (helperCore == null) return;
                    UserModel activeInbUser = mailDatabase.userDao().getActiveInbUser();
                    mailModel.mail_from = activeInbUser.email;
                    mailModel.user_id = activeInbUser.id;
                    FolderModel folderModel = mailDatabase.folderDao().getByUidAndType(
                        activeInbUser.id, FolderModel.FolderType.SENT.name()
                    );
                    helperCore.sendMail(folderModel, mailModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void replyMail(final MailModel fromMail, final MailModel replyTo, final boolean all) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                try {
                    HelperCore helperCore = getMailHelper();
                    if (helperCore == null) return;
                    UserModel activeInbUser = mailDatabase.userDao().getActiveInbUser();
                    fromMail.mail_from = activeInbUser.email;
                    fromMail.user_id = activeInbUser.id;
                    FolderModel folderModel = mailDatabase.folderDao().getByUidAndType(
                        activeInbUser.id, FolderModel.FolderType.SENT.name()
                    );
                    helperCore.replyMail(folderModel, fromMail, replyTo, all);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void forwardMail(final MailModel forwardTo) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                try {
                    HelperCore helperCore = getMailHelper();
                    if (helperCore == null) return;
                    UserModel activeInbUser = mailDatabase.userDao().getActiveInbUser();
                    forwardTo.mail_from = activeInbUser.email;
                    forwardTo.user_id = activeInbUser.id;
                    FolderModel folderModel = mailDatabase.folderDao().getByUidAndType(
                        activeInbUser.id, FolderModel.FolderType.SENT.name()
                    );
                    helperCore.sendMail(folderModel, forwardTo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public void archiveMail(final MailModel mailModel, final boolean archive) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                try {
                    HelperCore helperCore = getMailHelper();
                    if (helperCore == null) return;
                    if (!archive) helperCore.deleteMail(mailModel);
                    else {
                        UserModel activeInbUser = mailDatabase.userDao().getActiveInbUser();
                        FolderModel folderModel = mailDatabase.folderDao().getByUidAndType(
                            activeInbUser.id, FolderModel.FolderType.TRASH.name()
                        );
                        helperCore.trashMail(folderModel, mailModel, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public LiveData<MailModel> getByMailId(int msid) {
        return repo_dao.getByMailId(msid);
    }

    public LiveData<List<MailModel>> searchMessage(String search) {
        return repo_dao.searchMessage("%" + search + "%");
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
