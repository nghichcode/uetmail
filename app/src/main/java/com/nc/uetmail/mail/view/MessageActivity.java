package com.nc.uetmail.mail.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.adapters.MessageAdapter;
import com.nc.uetmail.mail.database.models.MessageModel;
import com.nc.uetmail.mail.viewmodel.MessageViewModel;
import com.nc.uetmail.main.utils.RecyclerViewSwipeDecorator;

import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private MessageViewModel messageViewModel;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity_note);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new MessageAdapter();
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getAllMessages().observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(@Nullable List<MessageModel> messageModels) {
                adapter.setMessages(messageModels);
            }
        });

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            messageViewModel.delete(adapter.getMessageAt(viewHolder.getAdapterPosition()));
            Toast.makeText(
                    MessageActivity.this, "Deleted", Toast.LENGTH_SHORT
            ).show();
        }

        @Override
        public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator
                .Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(MessageActivity.this, R.color.colorAccent))
                .addSwipeLeftActionIcon(R.drawable.ic_add)
                .setSwipeLeftLabelColor(ContextCompat.getColor(MessageActivity.this, R.color.colorWhite))
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
                messageViewModel.deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}