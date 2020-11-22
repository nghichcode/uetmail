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

    @Query("DELETE FROM mail_table WHERE user_id=:uid")
    void deleteByUid(int uid);

    @Query("SELECT * FROM mail_table WHERE id=:msid LIMIT 1")
    LiveData<MailModel> getByMailId(int msid);

    @Query("SELECT * FROM mail_table")
    LiveData<List<MailModel>> getAll();

//    @Query(
//        "SELECT m.* FROM mail_user_table u " +
//        "JOIN mail_folder_table f ON u.id=:uid AND f.user_id=u.id " +
//        "JOIN mail_table m ON f.id=m.folder_id WHERE 1"
//    )
//    LiveData<List<MailModel>> getMessagesInFolder(int uid);

    @Query("SELECT * FROM mail_table WHERE folder_id=:folderId ORDER BY mail_sent_date DESC")
    List<MailModel> getMailByFolderId(int folderId);

    @Query("SELECT m.* FROM mail_master_table ms JOIN mail_table m ON ms.active_folder_id=m.folder_id ORDER BY mail_sent_date DESC")
    LiveData<List<MailModel>> getLiveMailByActiveFolderId();

    @Query("SELECT m.* FROM mail_master_table ms JOIN mail_table m ON ms.active_folder_id=m.folder_id")
    List<MailModel> getMailByActiveFolderId();

//    @Query("SELECT m.* FROM mail_user_table u JOIN mail_folder_table f ON u.id=:uid AND f.user_id=u.id JOIN mail_table m ON f.id=m.folder_id WHERE 1")
//    LiveData<List<MailModel>> getMailByFolderType(int uid, String type);

    @Query("DELETE FROM mail_table WHERE sync=0")
    void deleteInvalidMail();

}
