package com.nc.uetmail.mail.database.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nc.uetmail.mail.async.AsyncTaskWithCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.session.MailHelper;
import com.nc.uetmail.mail.session.MailSession;

import java.util.List;

public class MailRepository {
    private MailDatabase mailDatabase;
    private MailDao repo_dao;
    private LiveData<List<MailModel>> messages;

    public MailRepository(Context context) {
        mailDatabase = MailDatabase.getInstance(context);
        repo_dao = mailDatabase.messageDao();
        messages = repo_dao.getAll();
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

    public void syncMail() {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                MailMasterDao masterDao = mailDatabase.mailMasterDao();
                UserDao userDao = mailDatabase.userDao();
                UserModel activeInbUser = userDao.getActiveInbUser();
                UserModel activeOubUser = userDao.getActiveOubUser();
                if (activeInbUser == null || activeOubUser == null) return;
                UserModel inb = UserRepository.decryptUser(masterDao, activeInbUser);
                UserModel oub = UserRepository.decryptUser(masterDao, activeOubUser);
                MailSession ms = null;
                try {
                    ms = MailSession.getInstance(inb, oub);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                try {
                    MailHelper helper = new MailHelper(ms, mailDatabase);
                    helper.listFolderAndMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public LiveData<List<MailModel>> getMessages() {
        return messages;
    }

}
