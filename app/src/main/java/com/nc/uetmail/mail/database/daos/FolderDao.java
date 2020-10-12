package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM mail_folder_table")
    LiveData<List<FolderModel>> getAll();

    @Query("DELETE FROM mail_folder_table WHERE 1")
    void deleteAll();

}
