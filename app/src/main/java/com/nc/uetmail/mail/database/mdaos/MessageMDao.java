package com.nc.uetmail.mail.database.mdaos;

import com.nc.uetmail.mail.database.models.MessageModel;
import com.nc.uetmail.mail.session.MailSession;

import java.util.List;

public class MessageMDao {
    private MailSession ms;

    public MessageMDao(final MailSession ms) {
        this.ms = ms;
    }

    void insert(MessageModel note) {

    }

    void update(MessageModel note) {

    }

    void delete(MessageModel note) {

    }

    List<MessageModel> getAll() {
        return null;
    }

}
