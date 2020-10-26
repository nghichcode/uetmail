package com.nc.uetmail.mail.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.nc.uetmail.mail.database.models.MasterModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.repository.MasterRepository;

public class MasterViewModel extends AndroidViewModel {
    private MasterRepository repository;

    public MasterViewModel(@NonNull Application application) {
        super(application);
        repository = new MasterRepository(application);
    }

    public void insert(MasterModel model){
        repository.insert(model);
    }
    public void update(MasterModel model){
        repository.update(model);
    }
    public void delete(MasterModel model){
        repository.delete(model);
    }

    public void setActiveUser(UserModel model){
        repository.setActiveUser(model.id);
    }
}
