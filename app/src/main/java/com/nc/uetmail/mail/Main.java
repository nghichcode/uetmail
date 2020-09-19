package com.nc.uetmail.mail;

import com.nc.uetmail.mail.session.MailHelper;
import com.nc.uetmail.mail.session.MailObject;
import com.nc.uetmail.mail.session.MailObject.ConnectionType;
import com.nc.uetmail.mail.session.MailObject.MailProtocol;
import com.nc.uetmail.mail.session.MailSession;

public class Main {
    public static void main(String[] args) {
//		SimpleMail.send();
//		SimpleMail.read();
//        MailObject inb = new MailObject(MailProtocol.IMAP.getName(), ConnectionType.SSL.getName(),
//                "nghichcode@nghichcode.com", "Zxcv1234", "imap.hostinger.vn", 993,
//                "nghichcode@nghichcode.com");
//        MailObject oub = new MailObject(MailProtocol.SMTP.getName(), ConnectionType.StartTLS.getName(),
//                "nghichcode@nghichcode.com", "Zxcv1234", "smtp.hostinger.vn", 587,
//                "nghichcode@nghichcode.com");
//        MailObject inb = new MailObject(MailProtocol.IMAP.getName(), ConnectionType.AUTH.getName(),
//            "16022428", "dataga", "uetmail.vnu.edu.vn", 143, "16022428@vnu.edu.vn");
//        MailObject oub = new MailObject(MailProtocol.SMTP.getName(), ConnectionType.AUTH.getName(),
//            "16022428", "dataga", "uetmail.vnu.edu.vn", 25, "16022428@vnu.edu.vn");
//        MailSession ms = new MailSession(inb, oub);
        MailObject inb = new MailObject(MailProtocol.IMAP.getName(), ConnectionType.AUTH.getName(),
                "", "Zxcv1234", "127.0.0.1", 143,
                "user@ncmail.com");
        MailObject oub = new MailObject(MailProtocol.SMTP.getName(), ConnectionType.AUTH.getName(),
                "", "Zxcv1234", "127.0.0.1", 25,
                "user@ncmail.com");
        MailSession ms = null;
        try {
            ms = MailSession.getInstance(inb, oub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MailHelper helper = new MailHelper(ms);

        try {
            helper.saveFolderAndMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
