package com.nc.uetmail.mail.database.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashSet;

@Entity(tableName = "mail_user_table")
public class UserModel extends BaseTimeModel {
    public enum MailProtocol {
        SMTP((byte) 1, "smtp"), POP3((byte) 3, "pop3"), IMAP((byte) 5, "imap"),
        GMAIL((byte) 88, "gmail");

        public final byte id;
        public final String name;

        MailProtocol(byte id, String name) {
            this.id = id;
            this.name = name;
        }

        public boolean eq(String str) {
            return name().equals(str);
        }
    }

    public enum ConnectionType {
        AUTH((byte) 6, "None"), SSL((byte) 8, "Secure (SSL)"), StartTLS((byte) 9, "Secure (TLS)");

        public final byte id;
        public final String name;

        ConnectionType(byte id, String name) {
            this.id = id;
            this.name = name;
        }

        public boolean eq(String str) {
            return name().equals(str);
        }
    }

    public enum DefaultPort {
        INB_AUTH(143), INB_SSL(993), INB_StartTLS(993),
        OUB_AUTH(25), OUB_SSL(465), OUB_StartTLS(587);

        public final int port;

        DefaultPort(final int port) {
            this.port = port;
        }
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String protocol;

    public String email;
    public String user;
    public String pass;
    public String hostname;
    public String type;
    public Integer port;

    public String iv;
    public boolean sync;
    public boolean incoming;

    public int target_id;
    public boolean valid_user;

    @Ignore
    public UserModel() {
    }

    @Ignore
    public UserModel(final String protocol, final String email, final String user,
                     final String pass, final String hostname, final String type,
                     final Integer port, final String iv, final boolean sync,
                     final boolean incoming, final int target_id) {
        this.protocol = protocol;
        this.email = email;
        this.user = user;
        this.pass = pass;
        this.hostname = hostname;
        this.type = type;
        this.port = port;
        this.iv = iv;
        this.sync = sync;
        this.incoming = incoming;
        this.target_id = target_id;
        this.valid_user = false;
    }

    public UserModel(final String protocol, final String email, final String user,
                     final String pass, final String hostname, final String type,
                     final Integer port, final String iv, final boolean sync,
                     final boolean incoming, final int target_id, final boolean valid_user) {
        this.protocol = protocol;
        this.email = email;
        this.user = user;
        this.pass = pass;
        this.hostname = hostname;
        this.type = type;
        this.port = port;
        this.iv = iv;
        this.sync = sync;
        this.incoming = incoming;
        this.target_id = target_id;
        this.valid_user = valid_user;
    }

    public static int getDefaultPort(String protocol, String type) {
        if (MailProtocol.IMAP.eq(protocol) || MailProtocol.POP3.eq(protocol)) {
            if (ConnectionType.SSL.eq(type)) {
                return DefaultPort.INB_SSL.port;
            } else if (ConnectionType.StartTLS.eq(type)) {
                return DefaultPort.INB_StartTLS.port;
            } else {
                return DefaultPort.INB_AUTH.port;
            }
        } else {
            if (ConnectionType.SSL.eq(type)) {
                return DefaultPort.OUB_SSL.port;
            } else if (ConnectionType.StartTLS.eq(type)) {
                return DefaultPort.OUB_StartTLS.port;
            } else {
                return DefaultPort.OUB_AUTH.port;
            }
        }
    }

    public String getFirstUserLetter() {
        return !"".equals(email) ? (email.toUpperCase().charAt(0) + "") : "?";
    }

    @Override
    protected String[] requireFields() {
        return new String[]{"protocol", "email", "user", "pass", "hostname", "type", "port", "sync",
            "incoming"};
    }

    public HashSet<String> validate() {
        HashSet<String> errors = super.validate();
        email = email.toLowerCase();
        String[] tmpHostname = email.split("@");
        if (tmpHostname.length == 2) {
            if (errors.contains("user")) {
                user = tmpHostname[0];
                errors.remove("user");
            }
            if (errors.contains("hostname")) {
                hostname = tmpHostname[1];
                errors.remove("hostname");
            }
        }
        if (errors.contains("type")) {
            type = ConnectionType.AUTH.name();
            errors.remove("type");
        }
        if (errors.contains("port")) {
            port = getDefaultPort(protocol, type);
            errors.remove("port");
        }
        return errors;
    }

    @Override
    public String toString() {
        return "UserModel{" +
            "id=" + id +
            ", protocol='" + protocol + '\'' +
            ", email='" + email + '\'' +
            ", user='" + user + '\'' +
            ", pass='" + pass + '\'' +
            ", hostname='" + hostname + '\'' +
            ", type='" + type + '\'' +
            ", port=" + port +
            ", iv='" + iv + '\'' +
            ", sync=" + sync +
            ", incoming=" + incoming +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}
