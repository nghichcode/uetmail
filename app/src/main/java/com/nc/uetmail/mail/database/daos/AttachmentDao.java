package com.nc.uetmail.mail.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nc.uetmail.mail.database.models.AttachmentModel;

import java.util.List;

@Dao
public interface AttachmentDao {
    @Insert
    void insert(AttachmentModel attachmentModel);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAttachments(List<AttachmentModel> attachmentModel);

    @Update
    void update(AttachmentModel attachmentModel);

    @Delete
    void delete(AttachmentModel attachmentModel);

    @Query("SELECT * FROM mail_attachment_table WHERE message_id=:msid AND html_source!=1")
    LiveData<List<AttachmentModel>> getByMessageId(int msid);

    @Query("SELECT * FROM mail_attachment_table")
    LiveData<List<AttachmentModel>> getAll();

}
