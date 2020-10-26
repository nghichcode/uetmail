package com.nc.uetmail.mail.database.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.MasterModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.utils.crypt.CryptoUtils;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback.*;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc.CryptData;

import java.util.List;

public class UserRepository {
    private UserDao userDao;

    private MailMasterDao masterDao;

    public UserRepository(Context context) {
        MailDatabase database = MailDatabase.getInstance(context);
        userDao = database.userDao();
        masterDao = database.mailMasterDao();

        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                userDao.deleteInvalidUser();
            }
        }).execute();
    }

    public void insert(final UserModel user) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                userDao.insert(user);
            }
        }).execute();
    }

    public void update(final UserModel user) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                userDao.update(user);
            }
        }).execute();
    }

    public void delete(final UserModel user) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                userDao.delete(user);
            }
        }).execute();
    }

    public void deleteById(final int id) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                userDao.deleteById(id);
            }
        }).execute();
    }

    public void insertFromRawPass(final UserModel inbModel, final UserModel oubModel) {
        new AsyncCallback(new CallbackInterface() {
            @Override
            public void call() {
                String message_digest = MasterRepository
                    .getOrCreateMasterModel(masterDao).message_digest;
                int uid = 0;
                try {
                    CryptData data = CryptorAesCbc.encryptWithKey(inbModel.pass, message_digest);
                    if (inbModel.pass.equals(oubModel.pass)) {
                        inbModel.pass = oubModel.pass = data.getText();
                        inbModel.iv = oubModel.iv = data.getIv();
                    } else {
                        inbModel.pass = data.getText();
                        inbModel.iv = data.getIv();
                        data = CryptorAesCbc.encryptWithKey(inbModel.pass, message_digest);
                        oubModel.pass = data.getText();
                        oubModel.iv = data.getIv();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                inbModel.valid_user = oubModel.valid_user = true;
                uid = (int) userDao.insert(inbModel);
                oubModel.target_id = uid;
                userDao.insert(oubModel);
                MasterModel masterModel = masterDao.getFirstMasterModel();
                masterModel.active_user_id = uid;
                masterDao.update(masterModel);
            }
        }).execute();

    }

    public static UserModel decryptUser(MailMasterDao masterDao, UserModel model) {
        String message_digest = MasterRepository
            .getOrCreateMasterModel(masterDao).message_digest;
        try {
            CryptData cryptData = new CryptData(
                CryptoUtils.hex2byte(message_digest),
                CryptoUtils.hex2byte(model.iv),
                CryptoUtils.hex2byte(model.pass)
            );
            model.pass = CryptorAesCbc.decrypt(cryptData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return model;
    }


    public LiveData<List<UserModel>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public LiveData<UserModel> getActiveInbUser() {
        return userDao.getActiveInbUserLive();
    }

}
