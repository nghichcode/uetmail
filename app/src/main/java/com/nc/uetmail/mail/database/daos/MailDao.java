package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM mail_table")
    LiveData<List<MailModel>> getAll();

    @Query("DELETE FROM mail_table WHERE 1")
    void deleteAll();

}
