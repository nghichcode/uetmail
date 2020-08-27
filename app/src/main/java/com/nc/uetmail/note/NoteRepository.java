package com.nc.uetmail.note;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    private enum ACTION {INSERT, UPDATE, DELETE, DELETE_ALL}

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insert(Note note){
        new UpdateNoteAsync(noteDao, ACTION.INSERT).execute(note);
    }
    public void update(Note note){
        new UpdateNoteAsync(noteDao, ACTION.UPDATE).execute(note);
    }
    public void delete(Note note){
        new UpdateNoteAsync(noteDao, ACTION.DELETE).execute(note);
    }
    public void deleteAllNotes(){
        new UpdateNoteAsync(noteDao, ACTION.DELETE_ALL).execute(
                new Note("","",0)
        );
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    private static class UpdateNoteAsync extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;
        private ACTION action;

        public UpdateNoteAsync(final NoteDao noteDao, ACTION action) {
            this.noteDao = noteDao;
            this.action = action;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            switch (action) {
                case INSERT:noteDao.insert(notes[0]);break;
                case UPDATE:noteDao.update(notes[0]);break;
                case DELETE:noteDao.delete(notes[0]);break;
                case DELETE_ALL:noteDao.deleteAllNotes();break;
            }

            return null;
        }
    }
}
