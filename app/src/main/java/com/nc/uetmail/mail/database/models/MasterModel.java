package com.nc.uetmail.mail.database.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "mail_master_table")
public class MasterModel extends BaseTimeModel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String message_digest;
    public boolean is_aes_key;
    public int active_user_id;
    public int active_folder_id;

    @Ignore
    public MasterModel(final String message_digest, boolean is_aes_key) {
        this.message_digest = message_digest;
        this.is_aes_key = is_aes_key;
    }

    public MasterModel(
        final String message_digest, final boolean is_aes_key,
        final int active_user_id, final int active_folder_id
    ) {
        this.message_digest = message_digest;
        this.is_aes_key = is_aes_key;
        this.active_user_id = active_user_id;
        this.active_folder_id = active_folder_id;
    }

    @Override
    public String toString() {
        return "MailMasterModel{" +
            "id=" + id +
            ", message_digest='" + message_digest + '\'' +
            ", is_aes_key=" + is_aes_key +
            ", active_user_id=" + active_user_id +
            ", active_folder_id=" + active_folder_id +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}
