package com.nc.uetmail.mail.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.nc.uetmail.mail.database.daos.*;
import com.nc.uetmail.mail.database.models.*;
import com.nc.uetmail.mail.database.repository.MailMasterRepository;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;
import com.nc.uetmail.mail.utils.interfaces.CallbackAsyncImpl;
import com.nc.uetmail.mail.utils.interfaces.CallbackInterface;

@Database(
        entities = {
                AttachmentModel.class, UserModel.class, MailModel.class, FolderModel.class,
                MailMasterModel.class
        },
        version = 1, exportSchema = false
)
public abstract class MailDatabase extends RoomDatabase {
    private static MailDatabase instance;

    public abstract AttachmentDao attachmentDao();

    public abstract UserDao userDao();

    public abstract MailDao messageDao();

    public abstract FolderDao folderDao();

    public abstract MailMasterDao mailMasterDao();

    public static synchronized MailDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MailDatabase.class, "mail_database.db"
            )
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }

    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new CallbackAsyncImpl(new CallbackInterface() {
                @Override
                public void callback() {
                    MailMasterRepository.getOrCreateMasterModel(instance.mailMasterDao());
                }
            }).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private MailMasterDao masterDao;

        private PopulateDbAsyncTask(MailDatabase mailDatabase) {
            masterDao = mailDatabase.mailMasterDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (masterDao.getFirstMasterModel().getValue() == null) {
                String message_digest = null;
                boolean is_aes_key = false;
                try {
                    message_digest = CryptorAesCbc.getAESKey();
                    is_aes_key = true;
                } catch (Exception e) {
                    message_digest = CryptorAesCbc.getRandomNonce();
                }
                masterDao.insert(new MailMasterModel(message_digest, is_aes_key));
            }
            return null;
        }
    }

}
