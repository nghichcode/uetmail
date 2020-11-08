package com.nc.uetmail.mail.session;

import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.nc.uetmail.mail.utils.crypt.CryptoUtils;

import org.apache.commons.codec.binary.Base64;

import javax.mail.Flags;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GMailHelper implements HelperCore {
    private MailDatabase database;
    private Gmail service;
    private final String user = "me";

    public GMailHelper(MailDatabase database, Gmail service) {
        this.database = database;
        this.service = service;
    }

    @Override
    public String saveMail(FolderModel folderModel, MailModel mailModel) throws Exception {
        return "";
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
        if (untrash) service.users().messages().untrash(user, mailModel.mail_uid).execute();
        else service.users().messages().trash(user, mailModel.mail_uid).execute();
//        System.out.println(mailModel);
        database.mailDao().update(mailModel);
    }

    @Override
    public void deleteMail(MailModel mailModel) throws Exception {
        if (mailModel == null || mailModel.mail_uid == null) return;
        service.users().messages().delete(user, mailModel.mail_uid).execute();
//        System.out.println(mailModel);
        database.mailDao().delete(mailModel);
    }

    private void send(
        FolderModel folderModel, MailModel newMail, MailModel replyMail, boolean replyAll
    ) throws Exception {
        if (newMail == null) return;
        newMail.mail_flags_code = Flags.Flag.DRAFT.hashCode();
        int newMailId = (int) database.mailDao().insert(newMail);

        HashMap<String, String> hm = new HashMap<>();
        if (replyMail != null) {
            Message replyTmp = service.users().messages().get(user, replyMail.mail_uid)
                .setFormat("metadata").execute();
            List<MessagePartHeader> hl = (replyTmp != null) && (replyTmp.getPayload() != null)
                ? replyTmp.getPayload().getHeaders() : null;
            hm = arrayToMap(hl);
        }
        MimeMessage email = MailMessage.createMailMessage(newMail, replyMail, hm, replyAll);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());
        Message sentMs = service.users().messages().send(user, new Message().setRaw(encodedEmail))
            .execute();
        if (sentMs != null && sentMs.getId() != null) {
            newMail.mail_flags_code = email.getFlags().hashCode();
            newMail.id = newMailId;
            newMail.mail_uid = sentMs.getId();
            newMail.folder_id = folderModel.id;
//        System.out.println(mailModel);
            database.mailDao().update(newMail);
        }
    }

    @Override
    public void listFolderAndMessage() throws Exception {
        List<FolderModel.FolderType> types = new ArrayList<FolderModel.FolderType>(
            Arrays.asList(FolderModel.FolderType.values())
        );

        ListLabelsResponse labelsResponse = service.users().labels().list(user).execute();
        List<Label> labels = labelsResponse.getLabels();
        if (!labels.isEmpty()) {
            for (Label label : labels) {
                Label curentLabel = service.users().labels().get(user, label.getId()).execute();
                Integer msgUnread = curentLabel.getMessagesUnread();
                Integer msgTotal = curentLabel.getMessagesTotal();
                if (msgTotal == null) continue;
                msgUnread = msgUnread == null ? 0 : msgUnread;
                FolderModel folderModel = new FolderModel(
                    -1, FolderModel.FolderType.OTHER.name(), label.getName(), label.getId(),
                    label.getName(), msgUnread, msgTotal, true, -1
                );
                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).matchType(folderModel.name)) {
                        folderModel.type = types.get(i).name();
                        folderModel.aliasName = types.get(i).name;
                        types.remove(i);
                        break;
                    }
                }

                ListMessagesResponse messagesResponse = service.users().messages().list(user)
                    .setLabelIds(Arrays.asList(new String[]{label.getId()})).execute();
                List<Message> messages = messagesResponse.getMessages();
                if (label.getName().toLowerCase().contains("inbox")) break;
//                System.out.println(folderModel);
                int folderId = (int) database.folderDao().insert(folderModel);
                if (messages == null) continue;
                for (Message message : messages) listMessage(message, folderId);
            }
        }
    }

    private void listMessage(Message message, int folderId) throws IOException {
        if (message == null) return;
        Message ms = message.getPayload() != null ? message :
            service.users().messages().get(user, message.getId()).execute();
        HashMap<String, String> hm = arrayToMap(ms.getPayload().getHeaders());
        SimpleDateFormat fm = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        Date sent_date;
        try {
            if (hm.get("Date") == null) sent_date = new Date();
            sent_date = fm.parse(hm.get("Date"));
        } catch (ParseException e) {
            sent_date = new Date();
        }

        String attachFolder = MailAndroidUtils.ROOT_FOLDER + File.separator + new Date().getTime();
        MailModel mailModel = new MailModel(-1, folderId, hm.get("Content-Type"),
            message.getId(), hm.get("Subject"), hm.get("From"), hm.get("To"), hm.get("Cc"),
            hm.get("Bcc"), "", "", false,
            attachFolder, false, 0, sent_date, null, true);
        List<AttachmentModel> attachments = new ArrayList<>();
        List<MessagePart> mp = ms.getPayload().getParts();
        for (int i = 0; i < mp.size(); i++)
            writePart(attachments, mailModel, mp.get(i));
        if (attachments.size() > 0) mailModel.mail_has_attachment = true;

        database.mailDao().insert(mailModel);
        for (AttachmentModel attachment : attachments) {
            attachment.message_id = mailModel.id;
            database.attachmentDao().insert(attachment);
        }
//        System.out.println(mailModel);
//        for (AttachmentModel attachmentModel : mailMessage.getAttachments())
//            System.out.println(attachmentModel);
    }

    private void writePart(List<AttachmentModel> attachments, MailModel mailModel, MessagePart p) throws IOException {
        if (p == null) return;
        writePart(attachments, mailModel, p, true);
    }

    private void writePart(List<AttachmentModel> attachments, MailModel mailModel, MessagePart p,
                           boolean download)
        throws IOException {
        if (p == null) return;
        ContentType type;
        try {
            type = new ContentType(p.getMimeType());
        } catch (javax.mail.internet.ParseException e) {
            e.printStackTrace();
            return;
        }

        if (type.match("text/plain")) {
            mailModel.mail_content_txt = StringUtils.newStringUtf8(p.getBody().decodeData());
        } else if (type.match("text/html")) {
            mailModel.mail_content_html = StringUtils.newStringUtf8(p.getBody().decodeData());
        } else if (type.match("multipart/*")) {
            List<MessagePart> mp = p.getParts();
            for (int i = 0; i < mp.size(); i++) writePart(attachments, mailModel, mp.get(i));
        } else {
            String fileName = p.getFilename();
            if (fileName == null || fileName.isEmpty()) return;

            String path = mailModel.attachments_folder;
            if (!new File(path).isDirectory()) new File(path).mkdirs();
            if (!new File(path).exists()) throw new IOException("Can not make dir.");
            String filePath = path + File.separator + fileName;
            try {
                new File(filePath).getCanonicalPath();
            } catch (IOException e) {
                System.err.println(e.toString());
                fileName = fileName.replaceAll("[:\\\\/*?|<>]", "_");
                filePath = path + File.separator + fileName;
            }

            HashMap<String, String> hm = arrayToMap(p.getHeaders());
            String contentID = hm.get("Content-ID");
            contentID = contentID != null ? contentID.replaceAll("<|>", "") : null;
            boolean html_source = (mailModel.mail_content_html == null ||
                mailModel.mail_content_html.isEmpty() ||
                !mailModel.mail_content_html.contains(contentID)
            ) ? false : true;
            String attachId = p.getBody() != null ? p.getBody().getAttachmentId() : null;
            String uri = MailAndroidUtils.ROOT_URI + filePath;
            if (html_source && mailModel.mail_content_html != null && !"".equals(mailModel
            .mail_content_html))
                mailModel.mail_content_html =
                    mailModel.mail_content_html.replaceAll("cid:" + contentID, fileName);
            attachments.add(new AttachmentModel(
                attachId, -1, p.getMimeType(), html_source, filePath,
                uri, (html_source || download), fileName,
                p.getBody().getSize(), hm.get("Content-Disposition"), contentID));

            if (hm.get("Content-Transfer-Encoding").equalsIgnoreCase("base64")
                && p.getBody().getSize() != 0 && (html_source || download)
            ) {
                if (attachId == null || attachId.isEmpty()) return;
                FileOutputStream fo = new FileOutputStream(filePath);
                MessagePartBody partBody = service.users().messages().attachments()
                    .get(user, mailModel.mail_uid, attachId).execute();
                String data = partBody.getData();
                if (data == null || data.isEmpty()) return;
                byte[] decoder = Base64.decodeBase64(data);
                fo.write(decoder);
                fo.close();
            }
        }


    }

    private HashMap<String, String> arrayToMap(List<MessagePartHeader> headers) {
        HashMap<String, String> hm = new HashMap<>();
        if (headers == null) return hm;
        for (MessagePartHeader header : headers) {
            hm.put(header.getName(), header.getValue());
        }
        return hm;
    }
}