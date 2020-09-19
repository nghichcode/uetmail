package com.nc.uetmail.mail.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.daos.MessageDao;
import com.nc.uetmail.mail.database.daos.FolderDao;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.MessageModel;
import com.nc.uetmail.mail.database.models.FolderModel;

@Database(
    entities = {UserModel.class, MessageModel.class, FolderModel.class},
    version = 1, exportSchema = false
)
public abstract class MailDatabase extends RoomDatabase {
    private static MailDatabase instance;
    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    public abstract FolderDao folderDao();
    public static synchronized MailDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MailDatabase.class, "mail_database"
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
        }
    };

}
