package com.nc.uetmail.mail.session.components;

import com.nc.uetmail.mail.database.models.MailModel;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import java.util.Date;
import java.util.HashMap;

public class MailMessage {
    private Message message;
    private MailModel mailModel;

    public MailModel getMailModel() {
        return this.mailModel;
    }

    public MailMessage(IMAPFolder imapFolder, Message message) throws MessagingException {
        this.message = message;

        long mUid = imapFolder.getUID(message);
        String mFrom = "";
        if (message.getFrom()!=null) {
            for (Address address : message.getFrom()) {
                mFrom+=((InternetAddress)address).getPersonal().isEmpty()
                    ? ((InternetAddress)address).getAddress()
                    : ((InternetAddress)address).getPersonal();
            }
        }
        HashMap<String, String> mCCMap = new HashMap<>();
        Address[] mrCC = message.getRecipients(Message.RecipientType.CC);
        if (mrCC!=null) {
            for (Address address : mrCC) {
                mCCMap.put(
                    ((InternetAddress) address).getAddress(),
                    ((InternetAddress) address).getPersonal()
                );
            }
        }
        String mSubject = message.getSubject();
        int mFlagsCode = message.getFlags().hashCode();
        Date mSentDate = message.getSentDate();
        Date mReceivedDate = message.getReceivedDate();

        String mTo ="";
        String mCC ="";
        String mBCC ="";
        String mTxt ="";
        String mHtml ="";
        boolean mHasAttachment =false;
        boolean mHasHtmlSource =false;
        this.mailModel = new MailModel(
            -1, -1, mUid, mSubject, mFrom, mTo , mCC, mBCC,
            mTxt, mHtml, mHasAttachment, mHasHtmlSource, mFlagsCode, mSentDate, mReceivedDate,
            true
        );
//        Log.i( "ToStr","NCMessageObject{" +
//            "mUid=" + mUid +
//            ",mFrom=" + mFrom +
//            ",mSubject=" + mSubject +
//            ",mFlags=" + mFrom +
//            ",mFlagsCode=" + mFlagsCode +
//            ",mSentDate=" + mSentDate +
//            ",mReceivedDate=" + mReceivedDate +
//        "}");
    }

}
