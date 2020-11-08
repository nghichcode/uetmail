package com.nc.uetmail.mail.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;

import java.util.List;

@Dao
public interface MailDao {
    @Insert
    long insert(MailModel mailModel);

    @Insert
    void insert(List<MailModel> mailModels);

    @Update
    void update(MailModel mailModel);

    @Delete
    void delete(MailModel mailModel);

    @Query("SELECT * FROM mail_table WHERE id=:msid LIMIT 1")
    LiveData<MailModel> getByMessageId(int msid);

    @Query("SELECT * FROM mail_table")
    LiveData<List<MailModel>> getAll();

    @Query("SELECT * FROM mail_table WHERE folder_id=:folderId")
    LiveData<List<MailModel>> getMailByFolderId(int folderId);

//    @Query("SELECT m.* FROM mail_user_table u JOIN mail_folder_table f ON u.id=:uid AND f.user_id=u.id JOIN mail_table m ON f.id=m.folder_id WHERE 1")
//    LiveData<List<MailModel>> getMailByFolderType(int uid, String type);

    @Query("DELETE FROM mail_table WHERE sync=0")
    void deleteInvalidMail();

}
