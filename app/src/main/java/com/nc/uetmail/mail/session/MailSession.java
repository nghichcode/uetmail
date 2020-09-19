package com.nc.uetmail.mail.session;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import com.nc.uetmail.mail.session.MailObject.ConnectionType;

public class MailSession {
    private static MailSession instance;

    private MailObject inbox;
    private MailObject outbox;
    
    private Session session;
    private Store store;

    public MailObject getInbox() {
        return inbox;
    }

    public MailObject getOutbox() {
        return outbox;
    }

    public Session getSession() {
        return session;
    }

    public Store getStore() {
        return this.store;
    }

    private MailSession(final MailObject inbox, MailObject outbox) throws Exception {
        this.inbox = inbox;
        this.outbox = outbox;

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(inbox.getUser(), inbox.getPass());
            }
        };

        Properties props = new Properties();
        String inProtocol = inbox.getProtocol();
        String ouProtocol = outbox.getProtocol();
        props.put("mail.store.protocol", inProtocol);

        props.put(String.format("mail.%s.auth", inProtocol), "true");
        props.put(String.format("mail.%s.host", inProtocol), inbox.getHostname());
        props.put(String.format("mail.%s.port", inProtocol), String.valueOf(inbox.getPort()));
        if (inbox.getType() == ConnectionType.StartTLS.getName() || inbox.getType() == ConnectionType.SSL.getName()) {
            if (inbox.getType() == ConnectionType.StartTLS.getName()) {
                props.put(String.format("mail.%s.starttls.enable", inProtocol), "true");
            } else {
                props.put(String.format("mail.%s.ssl.enable", inProtocol), "true");
            }

            props.setProperty(String.format("mail.%s.socketFactory.class", inProtocol), "javax.net.ssl.SSLSocketFactory");
            props.setProperty(String.format("mail.%s.socketFactory.fallback", inProtocol), "false");
            props.setProperty(String.format("mail.%s.socketFactory.port", inProtocol), String.valueOf(inbox.getPort()));
        }

        props.put(String.format("mail.%s.auth", ouProtocol), "true");
        props.put(String.format("mail.%s.host", ouProtocol), outbox.getHostname());
        props.put(String.format("mail.%s.port", ouProtocol), String.valueOf(outbox.getPort()));
        if (outbox.getType() == ConnectionType.StartTLS.getName() || outbox.getType() == ConnectionType.SSL.getName()) {
            if (outbox.getType() == ConnectionType.StartTLS.getName()) {
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

    public static MailSession getInstance(MailObject inbox, MailObject outbox) throws Exception {
        if(!inbox.isValid()) {return null;}
        if(outbox.getUser()==null || outbox.getUser()=="") {
            outbox.setUser(inbox.getUser());
        }
        if(outbox.getEmail()==null || outbox.getEmail()=="") {
            outbox.setEmail(inbox.getEmail());
        }
        if(outbox.getPass()==null || outbox.getPass()=="") {
            outbox.setPass(inbox.getPass());
        }
        if(!outbox.isValid()) {return null;}

        if (instance==null){
            instance = new MailSession(inbox, outbox);
        }
        return instance;
    }
}