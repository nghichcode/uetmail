package com.nc.uetmail.mail.database.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mail_attachment_table")
public class AttachmentModel extends BaseTimeModel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int user_id;

    public String attachment_id;
    public int message_id;
    public String content_type;
    public boolean html_source;
    public String path;
    public String uri;
    public boolean downloaded;
    public String name;
    public int size;
    public String content_description;
    public String content_id;

    public AttachmentModel(final String attachment_id, final int message_id,
                           final String content_type, final boolean html_source,
                           final String path, final String uri, final boolean downloaded,
                           final String name, final int size, final String content_description,
                           final String content_id) {
        this.attachment_id = attachment_id;
        this.message_id = message_id;
        this.content_type = content_type;
        this.html_source = html_source;
        this.path = path;
        this.uri = uri;
        this.downloaded = downloaded;
        this.name = name;
        this.size = size;
        this.content_description = content_description;
        this.content_id = content_id;
    }

    public String getSizeKb() {
        return (size > 0 ? size / 1024 : size) + " Kb";
    }

    @Override
    public String toString() {
        return "AttachmentModel{" +
            "id=" + id +
            "user_id=" + user_id +
            ", attachment_id='" + attachment_id + '\'' +
            ", message_id=" + message_id +
            ", content_type='" + content_type + '\'' +
            ", html_source=" + html_source +
            ", path='" + path + '\'' +
            ", uri='" + uri + '\'' +
            ", downloaded=" + downloaded +
            ", name='" + name + '\'' +
            ", size=" + size +
            ", content_description='" + content_description + '\'' +
            ", content_id='" + content_id + '\'' +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}
