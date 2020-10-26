package com.nc.uetmail.mail.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void insert(UserModel user) {
        repository.insert(user);
    }

    public void update(UserModel user) {
        repository.update(user);
    }

    public void delete(UserModel user) {
        repository.delete(user);
    }

    public LiveData<List<UserModel>> getUsers() {
        return repository.getAllUsers();
    }

    public LiveData<UserModel> getActiveInbUser() {
        return repository.getActiveInbUser();
    }
}
