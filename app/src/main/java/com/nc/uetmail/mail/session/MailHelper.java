package com.nc.uetmail.mail.session;

import android.content.Context;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.session.components.MailFolder;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailUtils;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MailHelper implements HelperCore {
    private Context context;
    private MailDatabase database;
    private MailSession session;
    private Store store;

    public MailHelper(Context context, MailDatabase database, MailSession session) throws Exception {
        this.context = context;
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
    public void seenMail(MailModel mailModel) throws Exception {
        Flags flags = new Flags(
            MailUtils.callPrivateConstructor(Flags.Flag.class, 0, mailModel.mail_flags_code)
        );
        flags.add(Flags.Flag.SEEN);
        mailModel.mail_flags_code = flags.hashCode();
        FolderModel folderModel = database.folderDao().getById(mailModel.folder_id);
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderModel.fullName);
        folder.open(Folder.READ_WRITE);
        long mail_uid = Long.parseLong(mailModel.mail_uid);
        Message ms = folder.getMessageByUID(mail_uid);
        ms.setFlag(Flags.Flag.SEEN, true);
        folder.close();
        database.mailDao().update(mailModel);
    }

    @Override
    public void sendMail(FolderModel folderModel, MailModel mailModel) throws Exception {
        send(folderModel, mailModel, null, false);
    }

    @Override
    public void replyMail(FolderModel folderModel, MailModel fromMail, MailModel replyTo,
                          boolean replyAll) throws Exception {
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
        MimeMessage email = MailMessage.createMailMessage(session.getSession(), mailModel, null,
            new HashMap<String, String>());
        mailModel.mail_uid = saveMail(folderModel, email);
        database.mailDao().update(mailModel);
    }

    @Override
    public void deleteMail(MailModel mailModel) throws Exception {
        if (mailModel == null) return;
        if (mailModel.mail_uid != null && !mailModel.mail_uid.isEmpty()) {
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

    private String saveMail(FolderModel folderModel, MimeMessage email) throws Exception {
        if (folderModel == null || email == null) return "";
        String ms_uid;
        try {
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

    private void send(FolderModel folderModel, MailModel newMail, MailModel replyMail,
                      boolean replyAll) throws Exception {
        if (newMail == null) return;
        newMail.mail_flags_code = new Flags(Flags.Flag.DRAFT).hashCode();
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
        MimeMessage email = MailMessage.createMailMessage(session.getSession(), newMail,
            replyMail, hm);
        if (email == null) throw new Exception("Mail empty.");
        newMail.mail_flags_code = email.getFlags().hashCode();

        Transport.send(email);
        String ms_uid = saveMail(folderModel, email);
        if (ms_uid != null && !ms_uid.isEmpty()) {
            newMail.mail_uid = ms_uid;
            newMail.id = newMailId;
//        System.out.println(mailModel);
            database.mailDao().update(newMail);
        }
    }


    @Override
    public void listFolderAndMail() throws Exception {
        int uid = session.getInbox().id;
        database.folderDao().deleteByUid(uid);
        database.mailDao().deleteByUid(uid);
        database.attachmentDao().deleteByUid(uid);

        Folder folder = store.getDefaultFolder();
        MailFolder mailFolder = new MailFolder(folder);
        List<FolderModel.FolderType> types = new ArrayList<FolderModel.FolderType>(
            Arrays.asList(FolderModel.FolderType.values())
        );
        listFolderAndMail(mailFolder, -1, types);
    }

    public void listFolderAndMail(
        MailFolder mailFolder, int parent_id, List<FolderModel.FolderType> types
    ) throws Exception {
        int folderId = -1;
        if (!mailFolder.isRoot()) {
            FolderModel folderModel = mailFolder.toFolderModel();
            folderModel.user_id = session.getInbox().id;
            folderModel.parent_id = parent_id;
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).matchType(folderModel.name)) {
                    folderModel.type = types.get(i).name();
                    folderModel.aliasName = types.get(i).name;
                    types.remove(i);
                    break;
                }
            }
//            System.out.println(folderModel);
            folderId = (int) database.folderDao().insert(folderModel);
            if (FolderModel.FolderType.INBOX.eq(folderModel.type)) {
                database.mailMasterDao().setActiveFolder(folderId);
            }

            for (MailMessage mailMessage : mailFolder.getMessages(context)) {
                MailModel mailModel = mailMessage.getMailModel();
                boolean seen = new Flags(
                    MailUtils.callPrivateConstructor(Flags.Flag.class, 0, mailModel.mail_flags_code)
                ).contains(Flags.Flag.SEEN);
                if (!seen) {
                    mailModel.nullToEmpty();
                    NotificationManagerCompat.from(context)
                        .notify(MailAndroidUtils.NOTIFICATION_ID + mailModel.id,
                            new NotificationCompat.Builder(context, context.getString(R.string.app_id))
                                .setSmallIcon(R.mipmap.mail_icon)
                                .setContentTitle(mailModel.mail_subject)
                                .setContentText(mailModel.getShortBodyTxt())
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                    mailModel.getMDBodyTxt()
                                ))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .build()
                        );
                }
                mailModel.user_id = session.getInbox().id;
                mailModel.folder_id = folderId;
                int message_id = (int) database.mailDao().insert(mailModel);
                for (AttachmentModel attachmentModel : mailMessage.getAttachments()) {
                    attachmentModel.user_id = session.getInbox().id;
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
                listFolderAndMail(fo, folderId, types);
            }
        }
    }
}
