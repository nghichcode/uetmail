package com.nc.uetmail.mail.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.MessageDao;
import com.nc.uetmail.mail.database.models.MessageModel;

import java.util.List;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<MessageModel>> allMessages;

    private enum ACTION {INSERT, UPDATE, DELETE, DELETE_ALL}

    public MessageRepository(Context context){
        MailDatabase database = MailDatabase.getInstance(context);
        messageDao = database.messageDao();
        allMessages = messageDao.getAll();
    }

    public void insert(MessageModel message){
        new UpdateMessageAsync(messageDao, ACTION.INSERT).execute(message);
    }
    public void update(MessageModel message){
        new UpdateMessageAsync(messageDao, ACTION.UPDATE).execute(message);
    }
    public void delete(MessageModel message){
        new UpdateMessageAsync(messageDao, ACTION.DELETE).execute(message);
    }
    public void deleteAllNotes(){
//        new UpdateMessageAsync(messageDao, ACTION.DELETE_ALL).execute(
//                new MessageModel()
//        );
    }

    public LiveData<List<MessageModel>> getAllMessages(){
        return allMessages;
    }

    private static class UpdateMessageAsync extends AsyncTask<MessageModel, Void, Void>{
        private MessageDao messageDao;
        private ACTION action;

        public UpdateMessageAsync(final MessageDao messageDao, ACTION action) {
            this.messageDao = messageDao;
            this.action = action;
        }

        @Override
        protected Void doInBackground(MessageModel... messages) {
            switch (action) {
                case INSERT:
                    messageDao.insert(messages[0]);break;
                case UPDATE:
                    messageDao.update(messages[0]);break;
                case DELETE:
                    messageDao.delete(messages[0]);break;
                case DELETE_ALL:
                    break;
            }

            return null;
        }
    }
}
