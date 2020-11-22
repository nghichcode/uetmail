package com.nc.uetmail.mail.database.repository;

import android.content.Context;

import com.nc.uetmail.mail.async.AsyncTaskWithCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.FolderDao;
import com.nc.uetmail.mail.database.models.FolderModel;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FolderRepository {
    private MailDatabase mailDatabase;
    private FolderDao repo_dao;

    public FolderRepository(Context context) {
        mailDatabase = MailDatabase.getInstance(context);
        repo_dao = mailDatabase.folderDao();
    }

    public LiveData<List<FolderModel>> getActiveFolders(String type) {
        return repo_dao.getActiveFolders(type);
    }

}
