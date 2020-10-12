package com.nc.uetmail.mail.database.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;
import com.nc.uetmail.mail.utils.interfaces.CallbackAsyncImpl;
import com.nc.uetmail.mail.utils.interfaces.CallbackInterface;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<UserModel>> allUsers;

    private MailMasterDao masterDao;

    private enum ACTION {INSERT, UPDATE, DELETE, DELETE_ALL}

    public UserRepository(Context context){
        MailDatabase database = MailDatabase.getInstance(context);
        userDao = database.userDao();
        masterDao = database.mailMasterDao();
        allUsers = userDao.getAll();
    }

    public void insert(UserModel user){
        new UpdateUserAsync(userDao, ACTION.INSERT, user).execute();
    }
    public void update(UserModel user){
        new UpdateUserAsync(userDao, ACTION.UPDATE, user).execute();
    }
    public void delete(UserModel user){
        new UpdateUserAsync(userDao, ACTION.DELETE, user).execute();
    }
    public void deleteAllNotes(){
        new UpdateUserAsync(userDao, ACTION.DELETE_ALL, null).execute();
    }
    public void insertFromRawPass(final UserModel userModel){
        new CallbackAsyncImpl(new CallbackInterface() {
            @Override
            public void callback() {
                String message_digest = MailMasterRepository.getOrCreateMasterModel(masterDao).message_digest;
                try {
                    CryptorAesCbc.CryptData data = CryptorAesCbc.encryptWithKey(
                            userModel.pass, message_digest
                    );
                    userModel.pass = data.getText();
                    userModel.iv = data.getIv();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userDao.insert(userModel);
            }
        }).execute();
    }

    public LiveData<List<UserModel>> getAllUsers(){
        return allUsers;
    }

    private static class UpdateUserAsync extends AsyncTask<Void, Void, Void>{
        private UserDao userDao;
        private ACTION action;
        private UserModel userModel;

        public UpdateUserAsync(final UserDao userDao, ACTION action, UserModel userModel) {
            this.userDao = userDao;
            this.action = action;
            this.userModel = userModel;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (userModel == null) return null;
            switch (action) {
                case INSERT:
                    userDao.insert(userModel);break;
                case UPDATE:
                    userDao.update(userModel);break;
                case DELETE:
                    userDao.delete(userModel);break;
                case DELETE_ALL:
                    userDao.deleteAll();break;
            }

            return null;
        }
    }

}
