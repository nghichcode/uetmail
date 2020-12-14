package com.nc.uetmail.mail.session;

import com.nc.uetmail.mail.database.models.UserModel;

public class UETMailConfig {
    public static UserModel inbox = new UserModel(
        UserModel.MailProtocol.IMAP.name(),
        "", "", "",
        "uetmail.vnu.edu.vn",
        UserModel.ConnectionType.AUTH.name(),
        143,
        "", true, true, 0, true
    );
    public static UserModel outbox = new UserModel(
        UserModel.MailProtocol.SMTP.name(),
        "", "", "",
        "uetmail.vnu.edu.vn",
        UserModel.ConnectionType.AUTH.name(),
        25,
        "", true, true, 0, true
    );
}
