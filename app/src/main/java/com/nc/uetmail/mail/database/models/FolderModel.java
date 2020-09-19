package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.nc.uetmail.mail.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "mail_folder_table")
public class FolderModel {
    public enum FolderType {
        INBOX((byte) 1, "Inbox", "inbox"), DRAFTS((byte) 2, "Drafts", "draft"),
        SENT((byte) 3, "Sent", "sent"), SPAM((byte) 4, "Spam", "spam|junk"),
        TRASH((byte) 5, "Trash", "trash|bin"), ARCHIVE((byte) 6, "Archive", "archive"),
        OTHER((byte) -1, "Other", "");

        private final byte id;
        private final String name;
        private final String regx;

        public byte getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        FolderType(byte id, String name, String regx) {
            this.id = id;
            this.name = name;
            this.regx = regx;
        }
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private byte type;
    private String name;
    private String fullName;
    private String aliasName;
    private int uid;

    @TypeConverters({DateConverter.class})
    private Date created_at;
    @TypeConverters({DateConverter.class})
    private Date updated_at;
    private boolean sync;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public int getUid() {
        return this.uid;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(final Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(final Date updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isSync() {
        return this.sync;
    }

    public FolderModel(final byte type, final String name, final String fullName, final String aliasName, final int uid, final Date created_at, final Date updated_at, final boolean sync) {
        this.type = type;
        this.name = name;
        this.fullName = fullName;
        this.aliasName = aliasName;
        this.uid = uid;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "FolderModel{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", uid=" + uid +
                '}';
    }

}
