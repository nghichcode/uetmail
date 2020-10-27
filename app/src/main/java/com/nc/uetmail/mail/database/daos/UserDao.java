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

    @Query("UPDATE mail_user_table SET valid_user = :is_valid WHERE id=:id OR target_id=:id")
    void validUserById(int id, boolean is_valid);

    @Delete
    void delete(UserModel userModel);

    @Query("DELETE FROM mail_user_table WHERE id=:id")
    void deleteById(int id);

    @Query("DELETE FROM mail_user_table WHERE valid_user = 0")
    void deleteInvalidUser();

    @Query("SELECT * FROM mail_user_table WHERE id=:id or target_id=:id LIMIT 2")
    LiveData<List<UserModel>> getUsersByIdOrTargetId(int id);

    @Query("SELECT * FROM mail_user_table WHERE id=:id")
    LiveData<UserModel> getUserById(int id);

    @Query("SELECT * FROM mail_user_table WHERE target_id=:id")
    LiveData<UserModel> getUserByTargetId(int id);

    @Query("SELECT * FROM mail_user_table WHERE incoming=1")
    LiveData<List<UserModel>> getAllUsers();

    @Query("SELECT u.* FROM mail_master_table m JOIN mail_user_table u ON m.active_user_id=u.id LIMIT 1")
    LiveData<UserModel> getActiveInbUserLive();

    @Query("SELECT u.* FROM mail_master_table m JOIN mail_user_table u ON m.active_user_id=u.id LIMIT 1")
    UserModel getActiveInbUser();

    @Query("SELECT u.* FROM mail_master_table m JOIN mail_user_table u ON m.active_user_id=u.target_id LIMIT 1")
    LiveData<UserModel> getActiveOubUserLive();

    @Query("SELECT u.* FROM mail_master_table m JOIN mail_user_table u ON m.active_user_id=u.target_id LIMIT 1")
    UserModel getActiveOubUser();

    @Query("DELETE FROM mail_user_table WHERE 1")
    void deleteAll();

}
