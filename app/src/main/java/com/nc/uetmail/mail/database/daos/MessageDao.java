package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nc.uetmail.mail.database.models.MessageModel;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(MessageModel note);

    @Update
    void update(MessageModel note);

    @Delete
    void delete(MessageModel note);

    @Query("SELECT * FROM mail_message_table")
    LiveData<List<MessageModel>> getAll();

}
