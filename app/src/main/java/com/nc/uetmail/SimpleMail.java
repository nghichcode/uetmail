package com.nc.uetmail;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

public class SimpleMail {

    public SimpleMail() {
    }

    public static void send() {
        System.out.println("Running...");
//		String fr = "nghichcode@nghichcode.com";
        String fr = "16022428@vnu.edu.vn";
        String to = "nghichcode@gmail.com";

//		final String user = "nghichcode@nghichcode.com";
//		final String pass = "Zxcv1234";
        final String user = "16022428";
        final String pass = "dataga";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "uetmail.vnu.edu.vn");
//		props.put("mail.smtp.host", "smtp.hostinger.vn");
//		props.put("mail.smtp.port", "587");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            System.out.println("Sending...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fr));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Subject Hello");
            message.setText("Hello...");
            Transport.send(message);

            System.out.println("Send success.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void read() {
//		final String user = "16022428";
//		final String pass = "dataga";
        final String user = "nghichcode@nghichcode.com";
        final String pass = "Zxcv1234";

        String protocol = "pop3";
        Properties props = new Properties();
//		props.put("mail.store.protocol", "imap");
//		props.put("mail.imap.auth", "true");
//		props.put("mail.imap.starttls.enable", "true");
//		props.put("mail.imap.host", "imap.hostinger.vn");
//		props.put("mail.imap.port", "993");

//		props.put("mail.store.protocol", protocol);
//		props.put("mail.imap.auth", "true");
//		props.put("mail.imap.host", "uetmail.vnu.edu.vn");
//		props.put("mail.imap.port", "143");

        props.put("mail.store.protocol", protocol);
        props.put("mail.pop3.auth", "true");
        props.put("mail.pop3.starttls.enable", "true");
        props.put("mail.pop3.host", "pop.hostinger.vn");
        props.put("mail.pop3.port", "995");

        props.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        props.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        props.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf("995"));

        Session session = Session.getDefaultInstance(props);

        Log.d("ncvnsm", "OK");
        try {
            Store store = session.getStore(protocol);
            store.connect(user, pass);

            Folder folder = store.getFolder("INBOX");
//			Folder folder = store.getDefaultFolder();
            folder.open(Folder.READ_WRITE);
            POP3Folder ufolder = (POP3Folder)folder;

            Message[] messages = folder.getMessages();

            System.out.println("messages.length: " + messages.length);
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                System.out.println("------------------");
                System.out.println("UID: " + ufolder.getUID(message));
                System.out.println("Flag: " + message.getFlags());
                message.setFlag(Flags.Flag.DELETED, true);
                writePart(message, i+1);
                break;
            }
            folder.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writePart(Part p) throws Exception {
        writePart(p, 99);
    }
    public static void writePart(Part p, int cnt) throws Exception {
        if (p instanceof Message)
            writeEnvelope((Message) p);
        System.out.println(":::: "+cnt);

        System.out.println("----------------------------");
        System.out.println("CONTENT-TYPE: " + p.getContentType());

        if (p.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------------");
            System.out.println((String) p.getContent());
        } else if (p.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) writePart(mp.getBodyPart(i));
        } else if (p.isMimeType("message/rfc822")) {
            System.out.println("This is a Nested Message");
            System.out.println("---------------------------");
            writePart((Part) p.getContent());
        } else if (p.isMimeType("image/jpeg")) {
            System.out.println("--------> image/jpeg");
            Object o = p.getContent();

            InputStream x = (InputStream) o;
            System.out.println("x.length = " + x.available());
            int i;
            byte[] bArray = new byte[x.available()];
            while ((i = (int) ((InputStream) x).available()) > 0) {
                int result = (int) (((InputStream) x).read(bArray));
                if (result == -1) {i = 0;break;}
            }
            FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
            f2.write(bArray);
            x.close();
            f2.close();
        } else if (p.getContentType().contains("image/")) {
            System.out.println("content type" + p.getContentType());
            File f = new File("image" + new Date().getTime() + ".jpg");
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = test.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
        } else {
            Object o = p.getContent();
            if (o instanceof String) {
                System.out.println("This is a string");
                System.out.println("---------------------------");
                System.out.println((String) o);
            } else if (o instanceof InputStream) {
                System.out.println("This is just an input stream");
                System.out.println("---------------------------");
                InputStream is = (InputStream) o;
//				is = (InputStream) o;
                int c;
                while ((c = is.read()) != -1) System.out.write(c);
                is.close();
            } else {
                System.out.println("This is an unknown type");
                System.out.println("---------------------------");
                System.out.println(o.toString());
            }
        }
    }

    public static void writeEnvelope(Message m) throws Exception {
        System.out.println("This is the message envelope");
        System.out.println("---------------------------");
        Address[] a;
        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++) System.out.println("FROM: " + a[j].toString());
        }
        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++) System.out.println("TO: " + a[j].toString());
        }
        // SUBJECT
        if (m.getSubject() != null) System.out.println("SUBJECT: " + m.getSubject());
    }

    static class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            SimpleMail.read();
            return "";
        }

        protected void onPostExecute(int i) {
        }
    }


}
