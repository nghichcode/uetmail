package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nc.uetmail.mail.database.models.UserModel;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(UserModel note);

    @Update
    void update(UserModel note);

    @Delete
    void delete(UserModel note);

    @Query("SELECT * FROM mail_user_table")
    LiveData<List<UserModel>> getAll();

}
