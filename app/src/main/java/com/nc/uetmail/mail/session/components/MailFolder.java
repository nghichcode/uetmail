package com.nc.uetmail.mail.session.components;

import android.content.Context;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.FolderModel.FolderType;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import java.util.ArrayList;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MailFolder {
    private boolean isRoot;
    private Folder folder;
    private FolderModel folderModel;

    private ArrayList<MailFolder> childrenFolders;
    private ArrayList<MailMessage> messages;

    public boolean isRoot() {
        return this.isRoot;
    }

    public ArrayList<MailFolder> getChildrenFolders() throws MessagingException {
        if (childrenFolders == null) {
            childrenFolders = new ArrayList<>();
            Folder[] folders = folder.list();
            for (int i = 0; i < folders.length; i++) {
                childrenFolders.add(new MailFolder(folders[i]));
            }
        }
        return childrenFolders;
    }

    public ArrayList<MailMessage> getMessages(Context context, int folderId, int userId, MailDatabase database) throws Exception {
        if (messages == null) {
            messages = new ArrayList<>();
            if (isRoot) return messages;
            folder.open(Folder.READ_ONLY);
            Message[] folderMessages = folder.getMessages();
            for (int i = 0; i < folderMessages.length; i++) {
//                this.messages.add(new MailMessage((IMAPFolder) folder, folderMessages[i], context));
                MailMessage mailMessage = new MailMessage(
                    (IMAPFolder) folder, folderMessages[i], context
                );
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
                mailModel.user_id = userId;
                mailModel.folder_id = folderId;
                int message_id = (int) database.mailDao().insert(mailModel);
                for (AttachmentModel attachmentModel : mailMessage.getAttachments()) {
                    attachmentModel.user_id = userId;
                    attachmentModel.message_id = message_id;
                    database.attachmentDao().insert(attachmentModel);
                }
            }
            folder.close();
        }
        return messages;
    }

    public MailFolder(Folder folder) throws MessagingException {
        this.folder = folder;
        if (folder.getName() == "") {
            isRoot = true;
        }
        int unreadMessageCount = 0;
        int messageCount = 0;
        if (!isRoot) {
            unreadMessageCount = folder.getUnreadMessageCount();
            messageCount = folder.getMessageCount();
        }
        folderModel = new FolderModel(
            -1, FolderType.OTHER.name(), folder.getName(), folder.getFullName(),
            "", unreadMessageCount, messageCount, true, -1
        );

    }

    public FolderModel toFolderModel() throws MessagingException {
        return folderModel;
    }

    @Override
    public String toString() {
        return "NCFolderObject{" +
            "name=" + folder.getName() +
            ", fullName=" + folder.getFullName() +
            ", childrenFolders=" + childrenFolders.size() +
            ", messageObjects=" + messages.size() +
            '}';
    }
}
