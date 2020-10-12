package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "mail_folder_table")
public class FolderModel extends BaseTimeModel {
    public enum FolderType {
        INBOX((byte) 1, "Inbox", "inbox"), DRAFTS((byte) 2, "Drafts", "draft"),
        SENT((byte) 3, "Sent", "sent"), SPAM((byte) 4, "Spam", "spam|junk"),
        TRASH((byte) 5, "Trash", "trash|bin"), ARCHIVE((byte) 6, "Archive", "archive"),
        OTHER((byte) -1, "Other", "");

        public final byte id;
        public final String name;
        public final String regx;

        FolderType(byte id, String name, String regx) {
            this.id = id;
            this.name = name;
            this.regx = regx;
        }

        public boolean eq(String str) {
            return name() == str;
        }
    }

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int user_id;

    public String type;
    public String name;
    public String fullName;
    public String aliasName;
    public int unread_count;
    public int message_count;
    public boolean sync;
    public int parent_id;

    public FolderModel(final int user_id, final String type, final String name,
                       final String fullName, final String aliasName,
                       final int unread_count, final int message_count, final boolean sync,
                       final int parent_id) {
        this.user_id = user_id;
        this.type = type;
        this.name = name;
        this.fullName = fullName;
        this.aliasName = aliasName;
        this.unread_count = unread_count;
        this.message_count = message_count;
        this.sync = sync;
        this.parent_id = parent_id;
    }

    @Override
    public String toString() {
        return "FolderModel{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", unread_count=" + unread_count +
                ", message_count=" + message_count +
                ", sync=" + sync +
                ", parent_id=" + parent_id +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
