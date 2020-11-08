package com.nc.uetmail.mail.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;

import com.nc.uetmail.mail.database.daos.*;
import com.nc.uetmail.mail.database.models.*;
import com.nc.uetmail.mail.database.repository.MasterRepository;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback.*;

@Database(
        entities = {
                AttachmentModel.class, UserModel.class, MailModel.class, FolderModel.class,
                MasterModel.class
        },
        version = 1, exportSchema = false
)
public abstract class MailDatabase extends RoomDatabase {
    private static MailDatabase instance;

    public abstract AttachmentDao attachmentDao();

    public abstract UserDao userDao();

    public abstract MailDao mailDao();

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
            new AsyncCallback(new CallbackInterface() {
                @Override
                public void call() {
                    MasterRepository.getOrCreateMasterModel(instance.mailMasterDao());
                }
            }).execute();
        }
    };

}
