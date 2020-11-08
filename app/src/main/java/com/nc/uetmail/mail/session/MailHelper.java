package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.session.components.MailFolder;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailUtils;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class MailHelper implements HelperCore {
    private MailDatabase database;
    private MailSession session;
    private Store store;

    public MailHelper(MailDatabase database, MailSession session) throws Exception {
        this.session = session;
        if (session == null) throw new Exception("Null Session");
        this.database = database;
        try {
            store = session.getSession().getStore();
            store.connect();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }

    @Override
    public String saveMail(FolderModel folderModel, MailModel mailModel) throws Exception {
        if (folderModel == null || mailModel == null) return "";
        String ms_uid;
        try {
            MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
            email.setFlag(MailUtils.callPrivateConstructor(
                Flags.Flag.class, 0, mailModel.mail_flags_code
            ), true);
            email.setSubject(mailModel.mail_subject);
            email.setFrom(new InternetAddress(mailModel.mail_from));
            email.setRecipient(RecipientType.TO, new InternetAddress(mailModel.mail_to));
            email.setRecipients(RecipientType.CC, MailMessage.toAddresses(mailModel.mail_cc));
            email.setRecipients(RecipientType.BCC, MailMessage.toAddresses(mailModel.mail_bcc));
            email.setText(mailModel.mail_content_txt);

            IMAPFolder folder = (IMAPFolder) store.getFolder(folderModel.fullName);
            folder.open(Folder.READ_WRITE);
            folder.appendMessages(new MimeMessage[]{email});
            ms_uid = folder.getUID(folder.getMessage(folder.getMessageCount() - 1)) + "";
            folder.close();
        } catch (MessagingException e) {
            throw new Exception(e.toString());
        }
        return ms_uid;
    }

    @Override
    public void sendMail(FolderModel folderModel, MailModel mailModel) throws Exception {
        mailModel.mail_uid = saveMail(folderModel, mailModel);
        send(folderModel, mailModel, null, false);
    }

    @Override
    public void replyMail(FolderModel folderModel, MailModel fromMail, MailModel replyTo,
                          boolean replyAll) throws Exception {
        fromMail.mail_uid = saveMail(folderModel, fromMail);
        send(folderModel, fromMail, replyTo, replyAll);
    }

    @Override
    public void forwardMail(FolderModel folderModel, MailModel fromMail, MailModel forwardTo)
        throws Exception {
        if (fromMail == null || forwardTo == null) return;
        fromMail.mail_subject = forwardTo.mail_subject;
        fromMail.mail_to = forwardTo.mail_to;
        fromMail.mail_cc = forwardTo.mail_cc;
        fromMail.mail_bcc = forwardTo.mail_bcc;
        send(folderModel, fromMail, null, false);
    }

    @Override
    public void trashMail(FolderModel folderModel, MailModel mailModel, boolean untrash) throws Exception {
        if (mailModel == null || mailModel.mail_uid == null) return;
        mailModel.mail_uid = saveMail(folderModel, mailModel);
        database.mailDao().update(mailModel);
    }

    @Override
    public void deleteMail(MailModel mailModel) throws Exception {
        if (mailModel==null) return;
        if (mailModel.mail_uid!=null && !mailModel.mail_uid.isEmpty()) {
            FolderModel folderModel = database.folderDao().getById(mailModel.folder_id);
            IMAPFolder folder = (IMAPFolder) store.getFolder(folderModel.fullName);
            folder.open(Folder.READ_WRITE);
            long mail_uid = Long.parseLong(mailModel.mail_uid);
            Message ms = folder.getMessageByUID(mail_uid);
            ms.setFlag(Flags.Flag.DELETED, true);
            folder.close();
        }
        database.mailDao().delete(mailModel);
    }

    private void send(FolderModel folderModel, MailModel newMail, MailModel replyMail,
                      boolean replyAll) throws Exception {
        if (newMail == null) return;
        newMail.mail_flags_code = Flags.Flag.DRAFT.hashCode();
        int newMailId = (int) database.mailDao().insert(newMail);

        HashMap<String, String> hm = new HashMap<>();
        if (replyMail != null) {
            IMAPFolder folder = (IMAPFolder) store.getFolder(folderModel.fullName);
            folder.open(Folder.READ_WRITE);
            long mail_uid = Long.parseLong(newMail.mail_uid);
            MimeMessage newMessage = (MimeMessage) folder.getMessageByUID(mail_uid);
            hm.put("References", newMessage.getMessageID());
            hm.put("In-Reply-To", newMessage.getMessageID());
        }
        MimeMessage email = MailMessage.createMailMessage(newMail, replyMail, hm, replyAll);
        newMail.mail_flags_code = email.getFlags().hashCode();

        Transport.send(email);
        String ms_uid = saveMail(folderModel, newMail);
        if (ms_uid != null && !ms_uid.isEmpty()) {
            newMail.mail_uid = ms_uid;
            newMail.id = newMailId;
//        System.out.println(mailModel);
            database.mailDao().update(newMail);
        }
    }


    @Override
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
            folderModel.user_id = session.getInbox().id;
            folderModel.parent_id = parent_id;
            for (int i = 0; i < types.size(); i++) {
                if (folderModel.name.matches(types.get(i).regx)) {
                    folderModel.type = types.get(i).name();
                    folderModel.aliasName = types.get(i).name;
                    types.remove(i);
                    break;
                }
            }
//            System.out.println(folderModel);
            folder_id = (int) database.folderDao().insert(folderModel);

            for (MailMessage mailMessage : mailFolder.getMessages()) {
                MailModel mailModel = mailMessage.getMailModel();
                mailModel.user_id = session.getInbox().id;
                mailModel.folder_id = folder_id;
                int message_id = (int) database.mailDao().insert(mailModel);
                for (AttachmentModel attachmentModel : mailMessage.getAttachments()) {
                    attachmentModel.message_id = message_id;
                    database.attachmentDao().insert(attachmentModel);
                }
//                System.out.println(mailModel);
//                for (AttachmentModel attachmentModel : mailMessage.getAttachments())
//                    System.out.println(attachmentModel);
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
