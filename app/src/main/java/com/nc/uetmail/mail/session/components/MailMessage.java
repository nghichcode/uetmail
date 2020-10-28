package com.nc.uetmail.mail.session.components;

import android.os.Environment;

import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.utils.crypt.CryptoUtils;
import com.sun.mail.imap.IMAPBodyPart;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.util.BASE64DecoderStream;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailMessage {
    private Message message;
    private MailModel mailModel;

    private List<AttachmentModel> attachments;

    public MailModel getMailModel() {
        return this.mailModel;
    }

    public List<AttachmentModel> getAttachments() {
        return this.attachments;
    }

    public MailMessage(IMAPFolder imapFolder, Message message) throws Exception {
        this.message = message;

        String content_type = message.getContentType();
        long mail_uid = imapFolder.getUID(message);
        String mail_subject = message.getSubject();
        String mail_from = getAddressString(message, null);
        String mail_to = getAddressString(message, RecipientType.TO);
        String mail_cc = getAddressString(message, RecipientType.CC);
        String mail_bcc = getAddressString(message, RecipientType.BCC);

        String mail_content_txt = "";
        String mail_content_html = "";
        boolean mail_has_attachment = false;
        boolean mail_has_html_source = false;
        int mail_flags_code = 0;
        Date mail_sent_date = null;
        Date mail_received_date = null;

        mailModel = new MailModel(
            -1, -1, content_type, mail_uid, mail_subject, mail_from, mail_to,
            mail_cc, mail_bcc, mail_content_txt, mail_content_html, mail_has_attachment,
            mail_has_html_source, mail_flags_code, mail_sent_date, mail_received_date, true
        );
        attachments = new ArrayList<>();

        try {
            writePart(message, mailModel);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }

    private void writePart(Part p, MailModel mailModel) throws MessagingException, IOException {
        if (p.isMimeType("text/plain")) {
            mailModel.mail_content_txt = (String) p.getContent();
        } else if (p.isMimeType("text/html")) {
            mailModel.mail_content_html = (String) p.getContent();
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) writePart(mp.getBodyPart(i), mailModel);
        } else {
            Object o = p.getContent();
            if (!(o instanceof BASE64DecoderStream || o instanceof InputStream)) return;
            String path = Environment.getExternalStorageDirectory()
                + File.separator + "uetmail_data";
            if (new File(path).isDirectory()) new File(path).mkdirs();
            if (!new File(path).exists()) throw new IOException("Can not make dir.");
            String filePath = path + File.separator + p.getFileName();
            try {
                new File(filePath).createNewFile();
            } catch (IOException e) {
                BODYSTRUCTURE bs = MailUtils.getPrivateAttr(p, "bs", BODYSTRUCTURE.class);
                filePath = path + File.separator
                    + CryptoUtils.byte2hex(CryptoUtils.getRandomNonce(16))
                    + "." + bs != null ? bs.subtype : "ext";
            }
            String contentID = ((IMAPBodyPart) p).getContentID();
            attachments.add(new AttachmentModel(
                -1, p.getContentType(),
                contentID != null, "", "", false, p.getFileName(),
                p.getSize(), p.getDisposition(), contentID));
            if (o instanceof BASE64DecoderStream) {
                File f = new File(filePath);
                DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f))
                );
                BASE64DecoderStream input = (BASE64DecoderStream) p.getContent();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
            } else if (o instanceof InputStream) {
                FileInputStream fi = (FileInputStream) p.getContent();
                FileOutputStream fo = new FileOutputStream(filePath);
                int i = 0;
                while ((i = fi.read()) != -1) {
                    fo.write(i);
                }
                fo.close();
            }
        }
    }

    public static Address[] getAddressFromString(String s) throws AddressException {
        if (s == null || s.trim().length() == 0) return null;
        String[] address_kvs = s.split(";");
        String emails = "";
        for (int i = 0; i < address_kvs.length; i++) {
            if (address_kvs[i] == null || address_kvs[i].trim().length() == 0) continue;
            String[] address_kv = address_kvs[i].split(":");
            if (address_kv.length == 2 || address_kv.length == 1) {
                String email = address_kv.length == 2 ? address_kv[1] : address_kv[0];
                if (email.split("@").length == 2) {
                    emails += (email + ",");
                }
            }
        }
        return InternetAddress.parse(emails);
    }

    private String getAddressString(Message message, RecipientType type) throws MessagingException {
        String address_str = "";
        Address[] addresses = null;
        if (type == null) {
            addresses = message.getFrom();
        } else {
            addresses = message.getRecipients(type);
        }
        if (addresses != null) {
            for (Address address : addresses) {
                address_str += (
                    ((InternetAddress) address).getPersonal() + ":"
                        + ((InternetAddress) address).getAddress() + ","
                );
            }
        }
        return address_str;
    }

}
