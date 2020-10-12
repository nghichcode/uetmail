package com.nc.uetmail.mail.database.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.models.MailModel;

import java.util.List;

public class MessageRepository {
    private MailDao mailDao;
    private LiveData<List<MailModel>> allMessages;

    private enum ACTION {INSERT, UPDATE, DELETE, DELETE_ALL}

    public MessageRepository(Context context){
        MailDatabase database = MailDatabase.getInstance(context);
        mailDao = database.messageDao();
        allMessages = mailDao.getAll();
    }

    public void insert(MailModel message){
        new UpdateMessageAsync(mailDao, ACTION.INSERT).execute(message);
    }
    public void update(MailModel message){
        new UpdateMessageAsync(mailDao, ACTION.UPDATE).execute(message);
    }
    public void delete(MailModel message){
        new UpdateMessageAsync(mailDao, ACTION.DELETE).execute(message);
    }
    public void deleteAllNotes(){
//        new UpdateMessageAsync(messageDao, ACTION.DELETE_ALL).execute(
//                new MessageModel()
//        );
    }

    public LiveData<List<MailModel>> getAllMessages(){
        return allMessages;
    }

    private static class UpdateMessageAsync extends AsyncTask<MailModel, Void, Void>{
        private MailDao mailDao;
        private ACTION action;

        public UpdateMessageAsync(final MailDao mailDao, ACTION action) {
            this.mailDao = mailDao;
            this.action = action;
        }

        @Override
        protected Void doInBackground(MailModel... messages) {
            switch (action) {
                case INSERT:
                    mailDao.insert(messages[0]);break;
                case UPDATE:
                    mailDao.update(messages[0]);break;
                case DELETE:
                    mailDao.delete(messages[0]);break;
                case DELETE_ALL:
                    break;
            }

            return null;
        }
    }
}
