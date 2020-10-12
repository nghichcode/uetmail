package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.session.components.MailFolder;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.database.daos.FolderDao;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MailHelper {
    private MailSession ms;
    private Store store;

    public Store getStore() {
        return store;
    }

    public MailHelper(MailSession ms) {
        this.ms = ms;
        try {
            store = ms.getSession().getStore();
            store.connect();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(Message message, String folderName) throws Exception {
        try {
            Message[] messageList= {message};
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            ((IMAPFolder) folder).appendMessages(messageList);
            folder.close();
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void sendMail(String to, String subject, String content) throws Exception {
        try {
            Message message = new MimeMessage(ms.getSession());
            message.setFrom(new InternetAddress(ms.getInbox().email));
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
            reply.setFrom(new InternetAddress(ms.getInbox().email));
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
            forward.setFrom(new InternetAddress(ms.getInbox().email));
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

    public void listFolderAndMessage(
            FolderDao folderDao, MailDao mailDao, UserModel user
    ) throws Exception {
        Folder folder = store.getDefaultFolder();
        MailFolder mailFolder = new MailFolder(folder);
        List<FolderModel.FolderType> types = new ArrayList<FolderModel.FolderType>(
                Arrays.asList(FolderModel.FolderType.values())
        );
        listFolderAndMessage(folderDao, mailDao, user, mailFolder, -1, types);
    }

    public void listFolderAndMessage(
            FolderDao folderDao, MailDao mailDao, UserModel user,
            MailFolder mailFolder, int parent_id, List<FolderModel.FolderType> types
    ) throws Exception {
        int folder_id = -1;
        if (!mailFolder.isRoot()) {
            FolderModel folderModel = mailFolder.toFolderModel();
            folderModel.user_id = user.id;
            folderModel.parent_id = parent_id;
            for (int i = 0; i < types.size(); i++) {
                if (folderModel.name.matches(types.get(i).regx)) {
                    folderModel.type=types.get(i).name();
                    folderModel.aliasName=types.get(i).name;
                    types.remove(i);
                    break;
                }
            }
            folder_id = (int)folderDao.insert(folderModel);

            ArrayList<MailModel> mailModels = new ArrayList<>();
            for (MailMessage ms: mailFolder.getMessages()){
                MailModel mailModel = ms.getMailModel();
                mailModel.user_id=user.id;
                mailModel.folder_id=folder_id;
                mailModels.add(mailModel);
            }
            mailDao.insert(mailModels);
        }

        ArrayList<MailFolder> childrenFolders = mailFolder.getChildrenFolders();
        if (childrenFolders!=null){
            for (MailFolder fo: childrenFolders){
                listFolderAndMessage(folderDao, mailDao, user, fo, folder_id, types);
            }
        }
    }
}
