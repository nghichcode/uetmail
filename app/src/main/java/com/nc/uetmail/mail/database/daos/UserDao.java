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
    long insert(UserModel userModel);

    @Update
    void update(UserModel userModel);

    @Delete
    void delete(UserModel userModel);

    @Query("SELECT * FROM mail_user_table WHERE id=:id")
    UserModel getUserModelById(int id);

    @Query("SELECT * FROM mail_user_table")
    LiveData<List<UserModel>> getAll();

    @Query("DELETE FROM mail_user_table WHERE 1")
    void deleteAll();

}
