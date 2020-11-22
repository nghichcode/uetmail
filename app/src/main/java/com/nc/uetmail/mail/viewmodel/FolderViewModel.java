package com.nc.uetmail.mail.viewmodel;

import android.app.Application;

import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.repository.FolderRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FolderViewModel extends AndroidViewModel {
    private FolderRepository repository;

    public FolderViewModel(@NonNull Application application) {
        super(application);
        repository = new FolderRepository(application);
    }

    public LiveData<List<FolderModel>> getActiveFolders(String type) {
        return repository.getActiveFolders(type);
    }
}
