package com.nc.uetmail.mail.viewmodel;

import android.app.Application;

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

    public void delete(MailModel message) {
        repository.delete(message);
    }

    public void syncMail() {
        repository.syncMail();
    }

    public LiveData<MailModel> getByMessageId(int msid) {
        return repository.getByMessageId(msid);
    }

    public LiveData<List<MailModel>> getMessages() {
        return repository.getMessages();
    }
}
