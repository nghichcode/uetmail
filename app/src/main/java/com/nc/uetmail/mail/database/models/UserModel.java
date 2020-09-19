package com.nc.uetmail.mail.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.nc.uetmail.mail.converters.DateConverter;
import com.nc.uetmail.mail.session.MailObject;

import java.util.Date;

@Entity(tableName = "mail_user_table")
public class UserModel {
    public void setId(final int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String email;

    private String in_protocol;
    private String in_type;
    private String in_user;
    private String in_pass;
    private String in_hostname;
    private Integer in_port;

    private String ou_protocol;
    private String ou_type;
    private String ou_user;
    private String ou_pass;
    private String ou_hostname;
    private Integer ou_port;

    @TypeConverters({DateConverter.class})
    private Date created_at;
    @TypeConverters({DateConverter.class})
    private Date updated_at;

    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIn_protocol() {
        return in_protocol;
    }

    public void setIn_protocol(String in_protocol) {
        this.in_protocol = in_protocol;
    }

    public String getIn_type() {
        return in_type;
    }

    public void setIn_type(String in_type) {
        this.in_type = in_type;
    }

    public String getIn_user() {
        return in_user;
    }

    public void setIn_user(String in_user) {
        this.in_user = in_user;
    }

    public String getIn_pass() {
        return in_pass;
    }

    public void setIn_pass(String in_pass) {
        this.in_pass = in_pass;
    }

    public String getIn_hostname() {
        return in_hostname;
    }

    public void setIn_hostname(String in_hostname) {
        this.in_hostname = in_hostname;
    }

    public Integer getIn_port() {
        return in_port;
    }

    public void setIn_port(Integer in_port) {
        this.in_port = in_port;
    }

    public String getOu_protocol() {
        return ou_protocol;
    }

    public void setOu_protocol(String ou_protocol) {
        this.ou_protocol = ou_protocol;
    }

    public String getOu_type() {
        return ou_type;
    }

    public void setOu_type(String ou_type) {
        this.ou_type = ou_type;
    }

    public String getOu_user() {
        return ou_user;
    }

    public void setOu_user(String ou_user) {
        this.ou_user = ou_user;
    }

    public String getOu_pass() {
        return ou_pass;
    }

    public void setOu_pass(String ou_pass) {
        this.ou_pass = ou_pass;
    }

    public String getOu_hostname() {
        return ou_hostname;
    }

    public void setOu_hostname(String ou_hostname) {
        this.ou_hostname = ou_hostname;
    }

    public Integer getOu_port() {
        return ou_port;
    }

    public void setOu_port(Integer ou_port) {
        this.ou_port = ou_port;
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

    public MailObject getInMailObject() {
        return new MailObject(in_protocol, in_type, in_user, in_pass, in_hostname, in_port, email);
    }

    public MailObject getOuMailObject() {
        return new MailObject(ou_protocol, ou_type, ou_user, ou_pass, ou_hostname, ou_port, email);
    }

    public UserModel(
        String email,
        String in_protocol, String in_type, String in_user, String in_pass, String in_hostname, Integer in_port,
        String ou_protocol, String ou_type, String ou_user, String ou_pass, String ou_hostname, Integer ou_port
    ) {
        this.email = email;
        this.in_protocol = in_protocol;
        this.in_type = in_type;
        this.in_user = in_user;
        this.in_pass = in_pass;
        this.in_hostname = in_hostname;
        this.in_port = in_port;
        this.ou_protocol = ou_protocol;
        this.ou_type = ou_type;
        this.ou_user = ou_user;
        this.ou_pass = ou_pass;
        this.ou_hostname = ou_hostname;
        this.ou_port = ou_port;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", in_protocol='" + in_protocol + '\'' +
                ", in_type='" + in_type + '\'' +
                ", in_user='" + in_user + '\'' +
                ", in_pass='" + in_pass + '\'' +
                ", in_hostname='" + in_hostname + '\'' +
                ", in_port=" + in_port +
                ", ou_protocol='" + ou_protocol + '\'' +
                ", ou_type='" + ou_type + '\'' +
                ", ou_user='" + ou_user + '\'' +
                ", ou_pass='" + ou_pass + '\'' +
                ", ou_hostname='" + ou_hostname + '\'' +
                ", ou_port=" + ou_port +
                '}';
    }
}
