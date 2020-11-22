package com.nc.uetmail.mail.database.repository;

import android.content.Context;

import com.nc.uetmail.mail.async.AsyncTaskWithCallback.CallbackInterface;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback.AsyncCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MasterModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;

public class MasterRepository {
    private MailDatabase database;
    private MailMasterDao repo_dao;

    public MasterRepository(Context context) {
        database = MailDatabase.getInstance(context);
        repo_dao = database.mailMasterDao();
    }

    public void insert(final MasterModel model) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.insert(model);
            }
        }).execute();
    }

    public void update(final MasterModel model) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.update(model);
            }
        }).execute();
    }

    public void delete(final MasterModel model) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.delete(model);
            }
        }).execute();
    }

    public void deleteAll() {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.deleteAll();
            }
        }).execute();
    }

    public void setActiveUser(final int uid) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.setActiveUser(uid);
            }
        }).execute();
    }

    public void setActiveFolder(final String type) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                UserModel userModel = database.userDao().getActiveInbUser();
                if (userModel == null) return;
                FolderModel folderModel = database.folderDao().getByUidAndType(userModel.id, type);
                repo_dao.setActiveFolder(folderModel == null ? -1 : folderModel.id);
            }
        }).execute();
    }

    public void setActiveFolderId(final int id) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                repo_dao.setActiveFolder(id);
            }
        }).execute();
    }

    public static MasterModel getOrCreateMasterModel(MailMasterDao repo_dao) {
        MasterModel masterModel = repo_dao.getFirstMasterModel();
        if (masterModel == null) {
            String message_digest = null;
            boolean is_aes_key = false;
            try {
                message_digest = CryptorAesCbc.getAESKey();
                is_aes_key = true;
            } catch (Exception e) {
                message_digest = CryptorAesCbc.getRandomNonce();
            }
            masterModel = new MasterModel(message_digest, is_aes_key);
            repo_dao.insert(masterModel);
        }
        return masterModel;
    }

}
