package com.nc.uetmail.note;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nc.uetmail.R;

import java.util.List;

import com.nc.uetmail.main.utils.RecyclerViewSwipeDecorator;

public class NoteActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQ = 1;
    public static final int EDT_NOTE_REQ = 2;

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity_note);

        FloatingActionButton actionButton = findViewById(R.id.btn_add_note);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQ);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(NoteActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddNoteActivity.EXTRA_DESC, note.getDescription());
                intent.putExtra(AddNoteActivity.EXTRA_PRIO, note.getPriority());
                startActivityForResult(intent, EDT_NOTE_REQ);
            }
        });
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
//                Toast.makeText(
//                    NoteActivity.this, "Changed", Toast.LENGTH_SHORT
//                ).show();
                adapter.setNotes(notes);
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQ && resultCode==RESULT_OK) {
            Note note = new Note(
                    data.getStringExtra(AddNoteActivity.EXTRA_TITLE),
                    data.getStringExtra(AddNoteActivity.EXTRA_DESC),
                    data.getIntExtra(AddNoteActivity.EXTRA_PRIO, 1)
            );
            noteViewModel.insert(note);
            Toast.makeText(
                    this, "Saved", Toast.LENGTH_SHORT
            ).show();
        } else if (requestCode == EDT_NOTE_REQ && resultCode==RESULT_OK) {
            int id = data.getIntExtra(AddNoteActivity.EXTRA_ID, -1);

            if (id!=-1) {
                Note note = new Note(
                        data.getStringExtra(AddNoteActivity.EXTRA_TITLE),
                        data.getStringExtra(AddNoteActivity.EXTRA_DESC),
                        data.getIntExtra(AddNoteActivity.EXTRA_PRIO, 1)
                );
                note.setId(id);
                noteViewModel.update(note);
                Toast.makeText(
                        this, "Updated", Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        this, "Can't update.", Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            Toast.makeText(
                    this, "Not Saved", Toast.LENGTH_SHORT
            ).show();
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
            Toast.makeText(
                    NoteActivity.this, "Deleted", Toast.LENGTH_SHORT
            ).show();
        }

        @Override
        public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator
                .Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.colorAccent))
                .addSwipeLeftActionIcon(R.drawable.ic_add)
                .setSwipeLeftLabelColor(ContextCompat.getColor(NoteActivity.this, R.color.colorWhite))
                .addSwipeLeftLabel("Add")
//                .addActionIcon(R.drawable.ic_close)
                .create()
                .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}