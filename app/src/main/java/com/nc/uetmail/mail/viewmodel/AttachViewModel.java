package com.nc.uetmail.mail.viewmodel;

import android.app.Application;

import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.repository.AttachRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AttachViewModel extends AndroidViewModel {
    private AttachRepository repository;

    public AttachViewModel(@NonNull Application application) {
        super(application);
        repository = new AttachRepository(application);
    }

    public void insert(AttachmentModel model) {
        repository.insert(model);
    }

    public void update(AttachmentModel model) {
        repository.update(model);
    }

    public void delete(AttachmentModel model) {
        repository.delete(model);
    }

    public LiveData<List<AttachmentModel>> getByMessageId(int msid) {
        return repository.getByMessageId(msid);
    }

    public LiveData<List<AttachmentModel>> getAttachments() {
        return repository.getAttachments();
    }
}
