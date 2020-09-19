package com.nc.uetmail.mail.components;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MailMessage {
    private Message message;

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
        HashMap<String, String> mCC = new HashMap<>();
        Address[] mrCC = message.getRecipients(Message.RecipientType.CC);
        if (mrCC!=null) {
            for (Address address : mrCC) {
                mCC.put(
                    ((InternetAddress) address).getAddress(),
                    ((InternetAddress) address).getPersonal()
                );
            }
        }
        String mSubject = message.getSubject();
        int mFlagsCode = message.getFlags().hashCode();
        Date mSentDate = message.getSentDate();
        Date mReceivedDate = message.getReceivedDate();


        System.out.println("NCMessageObject{" +
            "mUid=" + mUid +
            ",mFrom=" + mFrom +
            ",mSubject=" + mSubject +
            ",mFlags=" + mFrom +
            ",mFlagsCode=" + mFlagsCode +
            ",mSentDate=" + mSentDate +
            ",mReceivedDate=" + mReceivedDate +
        "}");

    }

}
