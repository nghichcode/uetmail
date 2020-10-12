package com.nc.uetmail.mail.session;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.UserModel.ConnectionType;
import com.nc.uetmail.mail.database.models.UserModel.MailProtocol;

public class MailSession {
    private static MailSession instance;

    private UserModel inbox;
    private UserModel outbox;
    
    private Session session;
    private Store store;

    public UserModel getInbox() {
        return inbox;
    }

    public UserModel getOutbox() {
        return outbox;
    }

    public Session getSession() {
        return session;
    }

    public Store getStore() {
        return this.store;
    }

    private MailSession(final UserModel inbox, UserModel outbox) throws Exception {
        this.inbox = inbox;
        this.outbox = outbox;

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(inbox.user, inbox.pass);
            }
        };

        Properties props = new Properties();
        String inProtocol = MailProtocol.valueOf(inbox.protocol).name;
        String ouProtocol = MailProtocol.valueOf(outbox.protocol).name;
        props.put("mail.store.protocol", inProtocol);

        props.put(String.format("mail.%s.auth", inProtocol), "true");
        props.put(String.format("mail.%s.host", inProtocol), inbox.hostname);
        props.put(String.format("mail.%s.port", inProtocol), String.valueOf(inbox.port));
        if ( ConnectionType.StartTLS.eq(inbox.type) || ConnectionType.SSL.eq(inbox.type)) {
            if (ConnectionType.StartTLS.eq(inbox.type)) {
                props.put(String.format("mail.%s.starttls.enable", inProtocol), "true");
            } else {
                props.put(String.format("mail.%s.ssl.enable", inProtocol), "true");
            }

            props.setProperty(String.format("mail.%s.socketFactory.class", inProtocol), "javax.net.ssl.SSLSocketFactory");
            props.setProperty(String.format("mail.%s.socketFactory.fallback", inProtocol), "false");
            props.setProperty(String.format("mail.%s.socketFactory.port", inProtocol), String.valueOf(inbox.port));
        }

        props.put(String.format("mail.%s.auth", ouProtocol), "true");
        props.put(String.format("mail.%s.host", ouProtocol), outbox.hostname);
        props.put(String.format("mail.%s.port", ouProtocol), String.valueOf(outbox.port));
        if (ConnectionType.StartTLS.eq(outbox.type) || ConnectionType.SSL.eq(outbox.type)) {
            if (ConnectionType.StartTLS.eq(outbox.type)) {
                props.put(String.format("mail.%s.starttls.enable", ouProtocol), "true");
            } else {
                props.put(String.format("mail.%s.ssl.enable", ouProtocol), "true");
            }
        }

        session = Session.getInstance(props, auth);
        try {
            store = session.getStore();
            store.connect();
        } catch (NoSuchProviderException e) {
            throw new Exception(e.getMessage());
        } catch (MessagingException e) {
            throw new Exception(e.getMessage());
        }
    }

    public static MailSession getInstance(UserModel inbox, UserModel outbox) throws Exception {
        if(!inbox.validate().isEmpty()) {return null;}
        if(outbox.user==null || outbox.user=="") {
            outbox.user=inbox.user;
        }
        if(outbox.email==null || outbox.email=="") {
            outbox.email=inbox.email;
        }
        if(outbox.pass==null || outbox.pass=="") {
            outbox.pass=inbox.pass;
        }
        if(!outbox.validate().isEmpty()) {return null;}

        if (instance==null){
            instance = new MailSession(inbox, outbox);
        }
        return instance;
    }
}