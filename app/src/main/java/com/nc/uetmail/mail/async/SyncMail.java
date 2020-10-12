package com.nc.uetmail.mail.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.FolderDao;
import com.nc.uetmail.mail.database.daos.MailDao;
import com.nc.uetmail.mail.database.daos.MailMasterDao;
import com.nc.uetmail.mail.database.daos.UserDao;
import com.nc.uetmail.mail.database.models.MailMasterModel;
import com.nc.uetmail.mail.session.MailHelper;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.session.MailSession;
import com.nc.uetmail.mail.utils.crypt.CryptoUtils;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc.CryptData;

public class SyncMail extends AsyncTask<Void, Void, Void> {
    Context ctx;
    UserModel inb;
    UserModel oub;

    public SyncMail(Context ctx) {
        this.ctx = ctx;
        // 10.0.2.2
        String host = "192.168.1.101";
//        String host = "10.0.2.2";
        inb = new UserModel(UserModel.MailProtocol.IMAP.name(), UserModel.ConnectionType.AUTH.name(),
                "", "Zxcv1234", "", host, 143, "user@ncmail.com", true,
                true);
        oub = new UserModel(UserModel.MailProtocol.SMTP.name(), UserModel.ConnectionType.AUTH.name(),
                "", "Zxcv1234", "", host, 25, "user@ncmail.com", true,
                false);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MailSession ms = null;
        MailDatabase database = MailDatabase.getInstance(ctx);
        MailMasterDao masterDao = database.mailMasterDao();
        try {
            CryptData encrypt = CryptorAesCbc.encryptWithKey(
                    inb.pass, masterDao.getFirstMasterModel().getValue().message_digest
            );
            inb.pass = CryptoUtils.byte2hex(encrypt.textBytes);
            inb.iv = CryptoUtils.byte2hex(encrypt.ivBytes);

            ms = MailSession.getInstance(inb, oub);
            if (ms!=null){
                MailHelper helper = new MailHelper(ms);
                FolderDao folderDao = database.folderDao();
                MailDao mailDao = database.messageDao();
                UserDao userDao = database.userDao();
                folderDao.deleteAll();
                mailDao.deleteAll();
                userDao.deleteAll();

                MailMasterModel mm=masterDao.getFirstMasterModel().getValue();
                inb.id = (int)userDao.insert(inb);
                oub.id = (int)userDao.insert(oub);
                helper.listFolderAndMessage(folderDao, mailDao, inb);
            }
        } catch (Exception e) {
            Log.d("SyncMail.constr", e.toString());
        }

        return null;
    }
}
