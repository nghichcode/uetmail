package com.nc.uetmail.mail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.nc.uetmail.R;

public class AddNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.nc.uetmail.note.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.nc.uetmail.note.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.nc.uetmail.note.EXTRA_DESC";
    public static final String EXTRA_PRIO = "com.nc.uetmail.note.EXTRA_PRIO";

    private EditText etTitle;
    private EditText etDescription;
    private NumberPicker npPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity_add_note);

        etTitle = findViewById(R.id.edit_text_title);
        etDescription = findViewById(R.id.edit_text_description);
        npPriority = findViewById(R.id.np_priority);

        npPriority.setMinValue(1);
        npPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent=getIntent();
        if (intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Note");
            etTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            etDescription.setText(intent.getStringExtra(EXTRA_DESC));
            npPriority.setValue(intent.getIntExtra(EXTRA_PRIO, 1));
        } else {
            setTitle("Add Note");
        }

    }

    private void saveNote() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        int priority = npPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Empty data...", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESC, description);
        data.putExtra(EXTRA_PRIO, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id!=-1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:saveNote();return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}