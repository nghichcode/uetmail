package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
    LiveData<MasterModel> getFirstMasterModelLive();

    @Query("SELECT * FROM mail_master_table WHERE 1 LIMIT 1")
    MasterModel getFirstMasterModel();

    @Query("DELETE FROM mail_master_table WHERE 1")
    void deleteAll();

    @Query("UPDATE mail_master_table SET active_user_id = :uid")
    void setActiveUser(int uid);

}
