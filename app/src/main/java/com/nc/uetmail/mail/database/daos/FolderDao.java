package com.nc.uetmail.mail.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nc.uetmail.mail.database.models.FolderModel;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert()
    long insert(FolderModel folderModel);

    @Update
    void update(FolderModel folderModel);

    @Delete
    void delete(FolderModel folderModel);

    @Query("DELETE FROM mail_folder_table WHERE user_id=:uid")
    void deleteByUid(int uid);

    @Query("SELECT * FROM mail_folder_table WHERE id=:id LIMIT 1")
    FolderModel getById(int id);

    @Query("SELECT * FROM mail_folder_table WHERE user_id=:uid AND type=:type LIMIT 1")
    FolderModel getByUidAndType(int uid, String type);

    @Query("SELECT f.* FROM mail_master_table m JOIN mail_folder_table f ON m.active_user_id=f.user_id AND f.type=:type")
    LiveData<List<FolderModel>> getActiveFolders(String type);

    @Query("DELETE FROM mail_folder_table WHERE 1")
    void deleteAll();

    @Query("SELECT * FROM mail_folder_table WHERE 1")
    List<FolderModel> getAll();

}