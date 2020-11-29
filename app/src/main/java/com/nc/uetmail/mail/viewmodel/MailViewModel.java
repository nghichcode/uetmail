package com.nc.uetmail.mail.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.repository.MailRepository;

import java.util.List;

public class MailViewModel extends AndroidViewModel {
    private MailRepository repository;

    public MailViewModel(@NonNull Application application) {
        super(application);
        repository = new MailRepository(application);
    }

    public void insert(MailModel message) {
        repository.insert(message);
    }

    public void update(MailModel message) {
        repository.update(message);
    }

    public void deleteEmptyActiveUserMs() {
        repository.deleteEmptyActiveUserMs();
    }

    public void delete(MailModel message) {
        repository.delete(message);
    }

    public void archiveMail(final MailModel mailModel, final boolean archive) {
        repository.archiveMail(mailModel, archive);
    }

    public void syncMail() {
        syncMail(false);
    }

    public void syncMail(boolean force) {
        repository.syncMail(force);
    }

    public void sendMail(MailModel mailModel) {
        repository.sendMail(mailModel);
    }

    public void seenMail(final MailModel mailModel) {
        repository.seenMail(mailModel);
    }

    public void replyMail(MailModel fromMail, MailModel replyTo, boolean all) {
        repository.replyMail(fromMail, replyTo, all);
    }

    public void forwardMail(MailModel forwardTo) {
        repository.forwardMail(forwardTo);
    }

    public LiveData<MailModel> getByMessageId(int msid) {
        return repository.getByMailId(msid);
    }

    public LiveData<List<MailModel>> searchMessage(String search) {
        return repository.searchMessage(search);
    }

    public LiveData<List<MailModel>> getMessages() {
        return repository.getMails();
    }

    public LiveData<List<MailModel>> getLiveMailByActiveFolderId() {
        return repository.getLiveMailByActiveFolderId();
    }

}
