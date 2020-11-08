package com.nc.uetmail.mail;

import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.UserModel.ConnectionType;
import com.nc.uetmail.mail.database.models.UserModel.MailProtocol;
import com.nc.uetmail.mail.session.MailSession;

public class Main {
    public static void main(String[] args) {
//		SimpleMail.send();
//		SimpleMail.read();
//        MailObject inb = new MailObject(MailProtocol.IMAP.getName(), ConnectionType.SSL.getName(),
//                "nghichcode@nghichcode.com", "Zxcv1234", "imap.hostinger.vn", 993,
//                "nghichcode@nghichcode.com");
//        MailObject oub = new MailObject(MailProtocol.SMTP.getName(), ConnectionType.StartTLS
//        .getName(),
//                "nghichcode@nghichcode.com", "Zxcv1234", "smtp.hostinger.vn", 587,
//                "nghichcode@nghichcode.com");
//        MailObject inb = new MailObject(MailProtocol.IMAP.getName(), ConnectionType.AUTH
//        .getName(),
//            "16022428", "dataga", "uetmail.vnu.edu.vn", 143, "16022428@vnu.edu.vn");
//        MailObject oub = new MailObject(MailProtocol.SMTP.getName(), ConnectionType.AUTH
//        .getName(),
//            "16022428", "dataga", "uetmail.vnu.edu.vn", 25, "16022428@vnu.edu.vn");
//        MailSession ms = new MailSession(inb, oub);
        UserModel inb = new UserModel(MailProtocol.IMAP.name, "user@ncmail.com", "", "Zxcv1234",
            "127.0.0.1", ConnectionType.AUTH.name, 143, "", false, true, 0);
        UserModel oub = new UserModel(MailProtocol.SMTP.name, "user@ncmail.com", "", "Zxcv1234",
            "127.0.0.1", ConnectionType.AUTH.name, 25, "", false, false, 0);
        MailSession ms = null;
    }
}
