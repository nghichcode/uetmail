package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nc.uetmail.mail.database.models.MailMasterModel;

@Dao
public interface MailMasterDao {
    @Insert
    long insert(MailMasterModel mailMasterModel);

    @Update
    void update(MailMasterModel mailMasterModel);

    @Delete
    void delete(MailMasterModel mailMasterModel);

    @Query("SELECT * FROM mail_master_table WHERE 1 LIMIT 1")
    LiveData<MailMasterModel> getFirstMasterModel();
    @Query("SELECT * FROM mail_master_table WHERE 1 LIMIT 1")
    MailMasterModel getFirstMasterModelSync();

    @Query("DELETE FROM mail_master_table WHERE 1")
    void deleteAll();

}
