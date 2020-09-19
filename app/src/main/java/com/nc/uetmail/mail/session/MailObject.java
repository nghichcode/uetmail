package com.nc.uetmail.mail.session;

public class MailObject {
    public enum MailProtocol {
        SMTP((byte) 1, "smtp"), POP3((byte) 3, "pop3"), IMAP((byte) 5, "imap");

        private final byte id;
        private final String name;

        public byte getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        MailProtocol(byte id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public enum ConnectionType {
        AUTH((byte) 6, "None"), SSL((byte) 8, "Secure (SSL)"), StartTLS((byte) 9, "Secure (TLS)");

        private final byte id;
        private final String name;

        public byte getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        ConnectionType(byte id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private String protocol;
    private String type;

    private String user;
    private String pass;
    private String hostname;
    private Integer port;
    private String email;

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setEmail(String email) {
        this.email = email;
        if(user.equals("")) {user=email;}
    }

    public String getProtocol() {
        return protocol;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    public String getEmail() {
        return email;
    }

    public MailObject(String protocol, String type, String user, String pass,
        String hostname, Integer port, String email) {
        this.protocol = protocol;
        this.type = type;
        this.user = user;
        this.pass = pass;
        this.hostname = hostname;
        this.port = port;
        this.email = email;
        if(user.equals("")) {this.user=email;}
    }

    public boolean isValid() {
        return (
            protocol != null && type != null && user != null && pass != null &&
            hostname != null && port != null && email!=null
        );
    }

    @Override
    public String toString() {
        return "MailObject [protocol=" + protocol + ", type=" + type + ", user=" + user + ", pass=" + pass
                + ", hostname=" + hostname + ", port=" + port + ", email=" + email + "]";
    }

}
