package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "mail_master_table")
public class MailMasterModel extends BaseTimeModel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String message_digest;
    public boolean is_aes_key;

    public MailMasterModel(final String message_digest, boolean is_aes_key) {
        this.message_digest = message_digest;
        this.is_aes_key = is_aes_key;
    }

    @Override
    public String toString() {
        return "MailMasterModel{" +
                "id=" + id +
                ", message_digest='" + message_digest + '\'' +
                ", is_aes_key=" + is_aes_key +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
