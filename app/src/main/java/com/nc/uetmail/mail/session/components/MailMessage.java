package com.nc.uetmail.mail.session.components;

import android.content.Context;

import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.nc.uetmail.mail.utils.crypt.CryptoUtils;
import com.sun.mail.imap.IMAPBodyPart;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.util.BASE64DecoderStream;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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

    public MailMessage(IMAPFolder imapFolder, Message message, Context context) throws Exception {
        this.message = message;

//        String attachFolder = MailAndroidUtils.ROOT_FOLDER + File.separator + new Date().getTime();
        String mail_uid = imapFolder.getUID(message) + "";
        String attachFolder = MailAndroidUtils.getRootFolder(context) + File.separator + "m"  + File.separator + mail_uid;
        mailModel = new MailModel(
            -1, -1, message.getContentType(), mail_uid,
            message.getSubject(), getAddressString(message, null),
            getAddressString(message, RecipientType.TO),
            getAddressString(message, RecipientType.CC),
            getAddressString(message, RecipientType.BCC),
            "", "", false, attachFolder,
            false, message.getFlags().hashCode(),
            message.getSentDate(), message.getReceivedDate(), true
        );
        attachments = new ArrayList<>();

        try {
            writePart(message, mailModel);
            if (attachments.size() > 0) mailModel.mail_has_attachment = true;
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }

    public void writePart(Part p, MailModel mailModel) throws MessagingException, IOException {
        writePart(p, mailModel, true);
    }

    public void writePart(Part p, MailModel mailModel, boolean download)
        throws MessagingException, IOException {
        if (p == null || mailModel == null) return;
        if (p.isMimeType("text/plain")) {
            mailModel.mail_content_txt = (String) p.getContent();
        } else if (p.isMimeType("text/html")) {
            mailModel.mail_content_html = (String) p.getContent();
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) writePart(mp.getBodyPart(i), mailModel);
        } else {
            Object o = p.getContent();
            if (!(o instanceof BASE64DecoderStream || o instanceof InputStream)) return;
            String fileName = p.getFileName();
            if (fileName == null || fileName.isEmpty()) return;
            BODYSTRUCTURE bs = MailUtils.getPrivateAttr(p, "bs", BODYSTRUCTURE.class);
            if (null != bs && null != bs.subtype && !fileName.contains("."))
                fileName += "." + bs.subtype.toLowerCase();
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

            String contentID = ((IMAPBodyPart) p).getContentID();
            contentID = contentID != null ? contentID.replaceAll("<|>", "") : null;
            boolean html_source = contentID != null;
            String uri = MailAndroidUtils.ROOT_URI + filePath;
            if (html_source && mailModel.mail_content_html != null && !"".equals(mailModel
                .mail_content_html))
                mailModel.mail_content_html =
                    mailModel.mail_content_html.replaceAll("cid:" + contentID, fileName);
            attachments.add(new AttachmentModel(
                "", -1, p.getContentType(), html_source,
                filePath, uri, (html_source || download),
                fileName, p.getSize(), p.getDisposition(), contentID));
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

    public static Address[] toAddresses(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        final String[] tmps = s.split(",");
        List<Address> addressList = new ArrayList<>();
        for (int i = 0; i < tmps.length; i++) {
            try {
                Address address = new InternetAddress(tmps[i].trim());
                addressList.add(address);
            } catch (AddressException e) {
                e.printStackTrace();
            }
        }
        return addressList.toArray(new Address[addressList.size()]);
    }

    public static String toAddressString(String ad) {
        String s = "";
        if (ad == null) return s;
        Address[] from = MailMessage.toAddresses(ad);
        if (from != null && from.length > 0) {
            for (int i = 0; i < from.length; i++) {
                s += ((InternetAddress) from[i]).getAddress().trim();
                if (i + 1 < from.length) s += ", ";
            }
        }
        return s;
    }

    public static String toPersonalString(String ad) {
        String s = "";
        if (ad == null) return s;
        Address[] from = MailMessage.toAddresses(ad);
        if (from != null && from.length > 0) {
            for (int i = 0; i < from.length; i++) {
                String tmp = ((InternetAddress) from[i]).getPersonal();
                if (tmp == null) tmp = ((InternetAddress) from[i]).getAddress();
                s += tmp;
                if (i + 1 < from.length) s += ", ";
            }
        }
        return s;
    }

    public static String toFullAddressString(String ad) {
        String s = "";
        if (ad == null) return s;
        Address[] from = MailMessage.toAddresses(ad);
        if (from != null && from.length > 0) {
            for (int i = 0; i < from.length; i++) {
                s += ((InternetAddress) from[i]).getPersonal()
                    + " (" + ((InternetAddress) from[i]).getAddress() + ")";
                if (i + 1 < from.length) s += ", ";
            }
        }
        return s;
    }

    private String getAddressString(Message message, RecipientType type) throws MessagingException {
        String address_str = "";
        Address[] addresses = type == null ? message.getFrom() : message.getRecipients(type);
        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                address_str += addresses[i].toString();
                if (i + 1 != addresses.length) address_str += ",";
            }
        }
        return address_str;
    }

    public static MimeMessage createMailMessage(
        Session session, MailModel newMail, MailModel replyMail, HashMap<String, String> headers
    ) throws MessagingException {
        if (session == null || newMail == null || headers == null) return null;
        MimeMessage email = new MimeMessage(session);
        Flags flags = new Flags(Flags.Flag.SEEN);
        email.setSubject(newMail.mail_subject);
        email.setFrom(new InternetAddress(newMail.mail_from));
        email.setRecipients(RecipientType.TO, MailMessage.toAddresses(newMail.mail_to));
        email.setRecipients(RecipientType.CC, MailMessage.toAddresses(newMail.mail_cc));
        email.setRecipients(RecipientType.BCC, MailMessage.toAddresses(newMail.mail_bcc));
        email.setSentDate(new Date());
        email.setText(newMail.mail_content_txt);

        if (replyMail != null) {
            email.setSubject(headers.get("Subject"));
            flags.add(Flags.Flag.ANSWERED);
            email.setHeader("References", headers.get("References"));
//            email.setHeader("Message-Id", headers.get("Message-Id"));
            email.setHeader("In-Reply-To", headers.get("Message-Id"));
        }
        email.setFlags(flags, true);
        return email;
    }
}
