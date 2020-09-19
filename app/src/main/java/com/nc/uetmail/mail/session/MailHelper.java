package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.components.MailFolder;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;

public class MailHelper {
    private MailSession ms;
    private Store store;

    public Store getStore() {
        return store;
    }

    public MailHelper(MailSession ms) {
        this.ms = ms;
        try {
            this.store = ms.getSession().getStore();
            this.store.connect();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(Message message) throws Exception {
        try {
            Message[] messageList= {message};
            Folder folder = store.getFolder("INBOX.Sent");
            folder.open(Folder.READ_WRITE);
            IMAPFolder ufolder = (IMAPFolder) folder;
            ufolder.appendMessages(messageList);
            ufolder.close();
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void sendMail(String to, String subject, String content) throws Exception {
        try {
            Message message = new MimeMessage(ms.getSession());
            message.setFrom(new InternetAddress(ms.getInbox().getEmail()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(content);
            message.setFlag(Flags.Flag.SEEN, true);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }
    
    public void replyMail(Message msg, String content, boolean replyAll) throws Exception {
        try {
            Message reply = new MimeMessage(ms.getSession());
            reply = (MimeMessage) msg.reply(replyAll);
            reply.setFrom(new InternetAddress(ms.getInbox().getEmail()));
            reply.setReplyTo(msg.getReplyTo());
            reply.setText(content);
            Transport.send(reply);
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void forwardMail(Message msg, String forwardTo) throws Exception {
        try {
            Message forward = new MimeMessage(ms.getSession());
            forward.setRecipients(Message.RecipientType.TO, InternetAddress.parse(forwardTo));
            forward.setFrom(new InternetAddress(ms.getInbox().getEmail()));
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart.setContent(msg, "message/rfc822");
            multipart.addBodyPart(messageBodyPart);
            forward.setContent(multipart, "UTF-8");
            forward.saveChanges();
            Transport.send(forward);
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void saveFolderAndMessage() throws Exception {
        Folder folder = store.getDefaultFolder();
        MailFolder folderObject = new MailFolder(folder);
        saveFolderAndMessage(folderObject);
    }

    public void saveFolderAndMessage(MailFolder folderObject) throws Exception {
        System.out.println(folderObject.toString());
        folderObject.getMessages();
        ArrayList<MailFolder> fos = folderObject.getChildrenFolders();
        if (fos!=null){
            for (MailFolder fo: fos){
                saveFolderAndMessage(fo);
            }
        }
    }
}
