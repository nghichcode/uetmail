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

    @Query("SELECT * FROM mail_folder_table WHERE id=:id LIMIT 1")
    FolderModel getById(int id);

    @Query("SELECT * FROM mail_folder_table")
    LiveData<List<FolderModel>> getAll();

    @Query("DELETE FROM mail_folder_table WHERE 1")
    void deleteAll();

}
