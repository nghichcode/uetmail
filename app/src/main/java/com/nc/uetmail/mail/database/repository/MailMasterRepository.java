package com.nc.uetmail.mail.database.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.models.MailMasterModel;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;

public class MailMasterRepository {
    private MailMasterDao repo_dao;

    private enum ACTION {INSERT, UPDATE, DELETE, DELETE_ALL}

    public MailMasterRepository(Context context){
        MailDatabase database = MailDatabase.getInstance(context);
        repo_dao = database.mailMasterDao();
    }

    public void insert(MailMasterModel model){
        new UpdateUserAsync(repo_dao, ACTION.INSERT, model).execute();
    }
    public void update(MailMasterModel model){
        new UpdateUserAsync(repo_dao, ACTION.UPDATE, model).execute();
    }
    public void delete(MailMasterModel model){
        new UpdateUserAsync(repo_dao, ACTION.DELETE, model).execute();
    }
    public void deleteAll(){
        new UpdateUserAsync(repo_dao, ACTION.DELETE_ALL, null).execute();
    }

    public static MailMasterModel getOrCreateMasterModel(MailMasterDao repo_dao) {
        MailMasterModel masterModel = repo_dao.getFirstMasterModelSync();
        if (masterModel == null) {
            String message_digest = null;
            boolean is_aes_key = false;
            try {
                message_digest = CryptorAesCbc.getAESKey();
                is_aes_key = true;
            } catch (Exception e) {
                message_digest = CryptorAesCbc.getRandomNonce();
            }
            masterModel = new MailMasterModel(message_digest, is_aes_key);
            repo_dao.insert(masterModel);
        }
        return masterModel;
    }

    public LiveData<MailMasterModel> getFirstMasterModel(){
        return repo_dao.getFirstMasterModel();
    }

    private static class UpdateUserAsync extends AsyncTask<Void, Void, Void>{
        private MailMasterDao dao;
        private ACTION action;
        private MailMasterModel model;

        public UpdateUserAsync(final MailMasterDao dao, ACTION action, MailMasterModel model) {
            this.dao = dao;
            this.action = action;
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (model == null) return null;
            switch (action) {
                case INSERT:
                    dao.insert(model);break;
                case UPDATE:
                    dao.update(model);break;
                case DELETE:
                    dao.delete(model);break;
                case DELETE_ALL:
                    dao.deleteAll();break;
            }

            return null;
        }
    }

}
