package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.models.AttachmentModel;
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
    private MailDatabase database;

    public Store getStore() {
        return store;
    }

    public MailHelper(MailSession ms, MailDatabase database) {
        this.ms = ms;
        this.database = database;
        try {
            store = ms.getSession().getStore();
            store.connect();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(Message message, String[] folderTree) throws Exception {
        try {
            Message[] messageList = {message};
            Folder folder = store.getDefaultFolder();
            for (int i = 0; i < folderTree.length; i++) {
                folder = folder.getFolder(folderTree[i]);
            }
            folder.open(Folder.READ_WRITE);
            ((IMAPFolder) folder).appendMessages(messageList);
            folder.close();
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void sendMail(MailModel mailModel) throws Exception {
        try {
            Message message = new MimeMessage(ms.getSession());
            message.setFrom(new InternetAddress(ms.getInbox().email));
            message.setRecipients(Message.RecipientType.TO, MailMessage.getAddressFromString(mailModel.mail_to));
            message.setRecipients(Message.RecipientType.CC, MailMessage.getAddressFromString(mailModel.mail_cc));
            message.setRecipients(Message.RecipientType.BCC, MailMessage.getAddressFromString(mailModel.mail_bcc));
            message.setSubject(mailModel.mail_subject);
            message.setSentDate(new Date());
            message.setText(mailModel.mail_content_txt);
            message.setFlag(Flags.Flag.SEEN, true);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
    }

    public void replyMail(MailModel mailModel, Message msg, boolean replyAll) throws Exception {
        try {
            Message reply;
            reply = (MimeMessage) msg.reply(replyAll);
            reply.setFrom(new InternetAddress(ms.getInbox().email));
            reply.setReplyTo(msg.getReplyTo());
            reply.setSubject(mailModel.mail_subject);
            reply.setSentDate(new Date());
            reply.setText(mailModel.mail_content_txt);
            reply.setFlag(Flags.Flag.SEEN, true);
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

    public void listFolderAndMessage() throws Exception {
        Folder folder = store.getDefaultFolder();
        MailFolder mailFolder = new MailFolder(folder);
        List<FolderModel.FolderType> types = new ArrayList<FolderModel.FolderType>(
            Arrays.asList(FolderModel.FolderType.values())
        );
        listFolderAndMessage(mailFolder, -1, types);
    }

    public void listFolderAndMessage(
        MailFolder mailFolder, int parent_id, List<FolderModel.FolderType> types
    ) throws Exception {
        int folder_id = -1;
        if (!mailFolder.isRoot()) {
            FolderModel folderModel = mailFolder.toFolderModel();
            folderModel.user_id = ms.getInbox().id;
            folderModel.parent_id = parent_id;
            for (int i = 0; i < types.size(); i++) {
                if (folderModel.name.matches(types.get(i).regx)) {
                    folderModel.type = types.get(i).name();
                    folderModel.aliasName = types.get(i).name;
                    types.remove(i);
                    break;
                }
            }
            folder_id = (int) database.folderDao().insert(folderModel);

            for (MailMessage mailMessage : mailFolder.getMessages()) {
                MailModel mailModel = mailMessage.getMailModel();
                mailModel.user_id = ms.getInbox().id;
                mailModel.folder_id = folder_id;
                int message_id = (int) database.messageDao().insert(mailModel);
                for (AttachmentModel attachmentModel : mailMessage.getAttachments()) {
                    attachmentModel.message_id = message_id;
                    database.attachmentDao().insert(attachmentModel);
                }
            }
        }

        ArrayList<MailFolder> childrenFolders = mailFolder.getChildrenFolders();
        if (childrenFolders != null) {
            for (MailFolder fo : childrenFolders) {
                listFolderAndMessage(fo, folder_id, types);
            }
        }
    }
}
