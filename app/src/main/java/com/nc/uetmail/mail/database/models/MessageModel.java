package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.nc.uetmail.mail.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "mail_message_table")
public class MessageModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int fid;

    private long m_uid;
    private String m_from;
    private String m_cc;
    private String m_bcc;
    private String m_to;

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public int getFid() {
        return this.fid;
    }

    public long getM_uid() {
        return this.m_uid;
    }

    public String getM_from() {
        return this.m_from;
    }

    public String getM_cc() {
        return this.m_cc;
    }

    public String getM_bcc() {
        return this.m_bcc;
    }

    public String getM_to() {
        return this.m_to;
    }

    public String getM_subject() {
        return this.m_subject;
    }

    public String getM_content_txt() {
        return this.m_content_txt;
    }

    public String getM_content_html() {
        return this.m_content_html;
    }

    public String getM_content_image() {
        return this.m_content_image;
    }

    public int getM_flags_code() {
        return this.m_flags_code;
    }

    public String getM_sent_date() {
        return this.m_sent_date;
    }

    public String getM_received_date() {
        return this.m_received_date;
    }

    public int getParent_id() {
        return this.parent_id;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }

    public boolean isSync() {
        return this.sync;
    }

    private String m_subject;
    private String m_content_txt;
    private String m_content_html;
    private String m_content_image;
    private int m_flags_code;
    private String m_sent_date;
    private String m_received_date;

    private int parent_id;

    @TypeConverters({DateConverter.class})
    private Date created_at;
    @TypeConverters({DateConverter.class})
    private Date updated_at;
    private boolean sync;

    public MessageModel(final int fid, final long m_uid, final String m_from, final String m_cc, final String m_bcc, final String m_to, final String m_subject, final String m_content_txt, final String m_content_html, final String m_content_image, final int m_flags_code, final String m_sent_date, final String m_received_date, final int parent_id, final Date created_at, final Date updated_at, final boolean sync) {
        this.fid = fid;
        this.m_uid = m_uid;
        this.m_from = m_from;
        this.m_cc = m_cc;
        this.m_bcc = m_bcc;
        this.m_to = m_to;
        this.m_subject = m_subject;
        this.m_content_txt = m_content_txt;
        this.m_content_html = m_content_html;
        this.m_content_image = m_content_image;
        this.m_flags_code = m_flags_code;
        this.m_sent_date = m_sent_date;
        this.m_received_date = m_received_date;
        this.parent_id = parent_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.sync = sync;
    }
}
