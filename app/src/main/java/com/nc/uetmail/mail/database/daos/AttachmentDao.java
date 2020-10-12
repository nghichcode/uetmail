package com.nc.uetmail.mail.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nc.uetmail.mail.database.models.AttachmentModel;

import java.util.List;

@Dao
public interface AttachmentDao {
    @Insert
    void insert(AttachmentModel attachmentModel);

    @Update
    void update(AttachmentModel attachmentModel);

    @Delete
    void delete(AttachmentModel attachmentModel);

    @Query("SELECT * FROM mail_attachment_table")
    LiveData<List<AttachmentModel>> getAll();

}
