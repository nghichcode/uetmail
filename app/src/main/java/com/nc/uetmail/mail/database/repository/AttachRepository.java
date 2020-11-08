package com.nc.uetmail.mail.database.repository;

import android.content.Context;

import com.nc.uetmail.mail.async.AsyncTaskWithCallback;
import com.nc.uetmail.mail.database.MailDatabase;
import com.nc.uetmail.mail.database.daos.AttachmentDao;
import com.nc.uetmail.mail.database.models.AttachmentModel;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AttachRepository {
    private MailDatabase mailDatabase;
    private AttachmentDao repo_dao;

    public AttachRepository(Context context) {
        mailDatabase = MailDatabase.getInstance(context);
        repo_dao = mailDatabase.attachmentDao();
    }

    public void insert(final AttachmentModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.insert(model);
            }
        }).execute();
    }

    public void update(final AttachmentModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.update(model);
            }
        }).execute();
    }

    public void delete(final AttachmentModel model) {
        new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
            @Override
            public void call() {
                repo_dao.delete(model);
            }
        }).execute();
    }

    public LiveData<List<AttachmentModel>> getByMessageId(int msid) {
        return repo_dao.getByMessageId(msid);
    }

    public LiveData<List<AttachmentModel>> getAttachments() {
        return repo_dao.getAll();
    }

}
