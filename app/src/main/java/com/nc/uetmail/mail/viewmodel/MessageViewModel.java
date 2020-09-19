package com.nc.uetmail.mail.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.nc.uetmail.mail.database.models.MessageModel;
import com.nc.uetmail.mail.repository.MessageRepository;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository repository;
    private LiveData<List<MessageModel>> allMessages;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        repository = new MessageRepository(application);
        allMessages = repository.getAllMessages();
    }

    public void insert(MessageModel message){
        repository.insert(message);
    }
    public void update(MessageModel message){
        repository.update(message);
    }
    public void delete(MessageModel message){
        repository.delete(message);
    }
    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<MessageModel>> getAllMessages(){
        return allMessages;
    }
}
