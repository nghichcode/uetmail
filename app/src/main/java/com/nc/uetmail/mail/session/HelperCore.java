package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;

public interface HelperCore {
    String saveMail(FolderModel folderModel, MailModel mailModel) throws Exception;

    void sendMail(FolderModel folderModel, MailModel mailModel) throws Exception;

    void replyMail(FolderModel folderModel, MailModel fromMail, MailModel replyTo, boolean replyAll)
        throws Exception;

    void forwardMail(FolderModel folderModel, MailModel fromMail, MailModel forwardTo)
        throws Exception;

    void trashMail(FolderModel folderModel, MailModel mailModel, boolean untrash) throws Exception;

    void deleteMail(MailModel mailModel) throws Exception;

    void listFolderAndMessage() throws Exception;

}
