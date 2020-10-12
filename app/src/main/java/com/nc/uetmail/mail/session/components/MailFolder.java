package com.nc.uetmail.mail.session.components;

import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.FolderModel.FolderType;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;

public class MailFolder {
    private boolean isRoot;
    private Folder folder;

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

    public ArrayList<MailMessage> getMessages() throws MessagingException {
        if (messages == null) {
            this.messages = new ArrayList<>();
            if (isRoot){ return messages; }
            folder.open(Folder.READ_ONLY);
            Message[] folderMessages = folder.getMessages();
            for (int i = 0; i < folderMessages.length; i++) {
                this.messages.add(new MailMessage((IMAPFolder) folder, folderMessages[i]));
            }
            folder.close();
        }
        return messages;
    }

    public MailFolder(Folder folder) {
        this.folder=folder;
        if (folder.getName()==""){ isRoot=true; }
    }

    public FolderModel toFolderModel() throws MessagingException {
        return new FolderModel(
            -1, FolderType.OTHER.name(), folder.getName(), folder.getFullName(),
            "", folder.getUnreadMessageCount(), folder.getMessageCount(), true, -1
        );
    }

    @Override
    public String toString() {
        return "NCFolderObject{" +
                "name=" + folder.getName() +
                ", fullName=" + folder.getFullName() +
                ", childrenFolders=" + childrenFolders +
                ", messageObjects=" + messages +
                '}';
    }
}
