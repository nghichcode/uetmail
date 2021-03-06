package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.nc.uetmail.mail.converters.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "mail_table")
public class MailModel extends BaseTimeModel {
    public static final int MAX_SHORT_SUBJECT = 32;
    public static final int MAX_SHORT_BODY = 128;
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int user_id;
    public int folder_id;
    public String content_type;

    public long mail_uid;
    public String mail_subject;
    public String mail_from;
    public String mail_to;
    public String mail_cc;
    public String mail_bcc;

    public String mail_content_txt;
    public String mail_content_html;
    public boolean mail_has_attachment;
    public boolean mail_has_html_source;
    public int mail_flags_code;
    @TypeConverters({DateConverter.class})
    public Date mail_sent_date;
    @TypeConverters({DateConverter.class})
    public Date mail_received_date;
    public boolean sync;

    @Ignore
    public MailModel() {
    }

    public MailModel(final int user_id, final int folder_id, String content_type,
                     final long mail_uid, final String mail_subject, final String mail_from,
                     final String mail_to, final String mail_cc, final String mail_bcc,
                     final String mail_content_txt, final String mail_content_html,
                     final boolean mail_has_attachment, final boolean mail_has_html_source,
                     final int mail_flags_code, final Date mail_sent_date,
                     final Date mail_received_date, final boolean sync) {
        this.user_id = user_id;
        this.folder_id = folder_id;
        this.content_type = content_type;
        this.mail_uid = mail_uid;
        this.mail_subject = mail_subject;
        this.mail_from = mail_from;
        this.mail_to = mail_to;
        this.mail_cc = mail_cc;
        this.mail_bcc = mail_bcc;
        this.mail_content_txt = mail_content_txt;
        this.mail_content_html = mail_content_html;
        this.mail_has_attachment = mail_has_attachment;
        this.mail_has_html_source = mail_has_html_source;
        this.mail_flags_code = mail_flags_code;
        this.mail_sent_date = mail_sent_date;
        this.mail_received_date = mail_received_date;
        this.sync = sync;
    }

    public String getFirstUserLetter() {
        return !"".equals(mail_from) ? (mail_from.charAt(0) + "") : "?";
    }

    public String getShortSubject() {
        int len = mail_subject.length() < MAX_SHORT_SUBJECT ?
            mail_subject.length() : MAX_SHORT_SUBJECT;
        if (len < 1) return "";
        return mail_subject.substring(0, len) + "...";
    }

    public String getShortBodyTxt() {
        String short_content_txt = mail_content_txt.replaceAll("\n","");
        int len = short_content_txt.length() < MAX_SHORT_BODY ? short_content_txt.length() : MAX_SHORT_BODY;
        if (len < 1) return "";
        return short_content_txt.substring(0, len) + "...";
    }

    public String getFormatDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(mail_sent_date);
    }

    public MailModel clone() {
        return new MailModel(
            user_id, folder_id, content_type, mail_uid, mail_subject, mail_from, mail_to, mail_cc
            , mail_bcc, mail_content_txt, mail_content_html, mail_has_attachment,
            mail_has_html_source, mail_flags_code, mail_sent_date, mail_received_date, sync
        );
    }

    @Override
    public String toString() {
        return "MailModel{" +
            "id=" + id +
            ", user_id=" + user_id +
            ", folder_id=" + folder_id +
            ", content_type=" + content_type +
            ", mail_uid=" + mail_uid +
            ", mail_subject='" + mail_subject + '\'' +
            ", mail_from='" + mail_from + '\'' +
            ", mail_to='" + mail_to + '\'' +
            ", mail_cc='" + mail_cc + '\'' +
            ", mail_bcc='" + mail_bcc + '\'' +
            ", mail_content_txt='" + mail_content_txt + '\'' +
            ", mail_content_html='" + mail_content_html + '\'' +
            ", mail_has_attachment=" + mail_has_attachment +
            ", mail_has_html_source=" + mail_has_html_source +
            ", mail_flags_code=" + mail_flags_code +
            ", mail_sent_date=" + mail_sent_date +
            ", mail_received_date=" + mail_received_date +
            ", sync=" + sync +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}
