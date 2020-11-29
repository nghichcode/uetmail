package com.nc.uetmail.mail.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.adapters.MailRecyclerAdapter;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.viewmodel.MailViewModel;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBar.LayoutParams;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchMailActivity extends AppCompatActivity {
    private MailRecyclerAdapter messageAdapter;
    private RecyclerView rvListEmail;
    private MailViewModel mailViewModel;

    private EditText edSearch;
    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_search_activity);
        LinearLayout llToolbarBody = (LinearLayout) LayoutInflater
            .from(this)
            .inflate(R.layout.mail_search_toolbar_body, null);
        edSearch = llToolbarBody.findViewById(R.id.mail_search_toolbar_input);
        btnSearch = llToolbarBody.findViewById(R.id.mail_search_toolbar_search_btn);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowCustomEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setCustomView(
                llToolbarBody,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            );
        }

        mailViewModel = ViewModelProviders.of(this).get(MailViewModel.class);
        rvListEmail = findViewById(R.id.mail_search_list_email);
        rvListEmail.setLayoutManager(new LinearLayoutManager(this));
        rvListEmail.setHasFixedSize(true);
        rvListEmail.addItemDecoration(
            new DividerItemDecoration(rvListEmail.getContext(),
                DividerItemDecoration.VERTICAL)
        );
        messageAdapter = new MailRecyclerAdapter();
        messageAdapter.setOnItemClickListener(new MailRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MailModel model, int position) {
                Intent intent = new Intent(SearchMailActivity.this, MessageSlideActivity.class);
                intent.putExtra(HomeActivity.EXTRA_MESSAGE_ID, model.id);
                intent.putExtra(HomeActivity.EXTRA_MESSAGE_POS, -1);
                startActivityForResult(intent, HomeActivity.REQUEST_MESSAGE);
            }
        });
        rvListEmail.setAdapter(messageAdapter);

        edSearch.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int maxPos = edSearch.getRight() - edSearch
                        .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= maxPos) {
                        edSearch.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edSearch.getText().toString();
                if (search == null || "".equals(search)) return;
                mailViewModel.searchMessage(search).observe(SearchMailActivity.this,
                    new Observer<List<MailModel>>() {
                        @Override
                        public void onChanged(List<MailModel> models) {
                            messageAdapter.setMessages(models);
                        }
                    });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}