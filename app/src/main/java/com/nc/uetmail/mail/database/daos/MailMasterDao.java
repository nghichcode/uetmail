package com.nc.uetmail.mail.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nc.uetmail.mail.database.models.MasterModel;

@Dao
public interface MailMasterDao {
    @Insert
    long insert(MasterModel masterModel);

    @Update
    void update(MasterModel masterModel);

    @Delete
    void delete(MasterModel masterModel);

    @Query("SELECT * FROM mail_master_table WHERE 1 LIMIT 1")
    MasterModel getFirstMasterModel();

    @Query("DELETE FROM mail_master_table WHERE 1")
    void deleteAll();

    @Query("UPDATE mail_master_table SET active_user_id = :uid")
    void setActiveUser(int uid);

    @Query("UPDATE mail_master_table SET active_folder_id = :fid")
    void setActiveFolder(int fid);

    @Query("UPDATE mail_master_table SET active_user_id=:new_uid WHERE active_user_id=:old_uid")
    void setActiveUserIfNull(int old_uid, int new_uid);

}
