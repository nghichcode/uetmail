package com.nc.uetmail.note;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private  int id;

    private String title;
    private String description;
    private int priority;

    public Note(final String title, final String description, final int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPriority() {
        return this.priority;
    }

}
