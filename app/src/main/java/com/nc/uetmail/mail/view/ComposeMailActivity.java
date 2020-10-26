package com.nc.uetmail.mail.view;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nc.uetmail.R;

public class ComposeMailActivity extends AppCompatActivity {
    private boolean isShowAdvance = true;

    private ImageButton btnComposeExpand;
    private TextView tvComposeTo;
    private TextView tvComposeCC;
    private TextView tvComposeBCC;
    private TextView tvComposeSubject;
    private TextView tvCompose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_compose_activity);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnComposeExpand = findViewById(R.id.mail_compose_expand);
        btnComposeExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvance();
            }
        });
        tvComposeTo = findViewById(R.id.mail_compose_to);
        tvComposeCC = findViewById(R.id.mail_compose_cc);
        tvComposeBCC = findViewById(R.id.mail_compose_bcc);
        tvComposeSubject = findViewById(R.id.mail_compose_subject);
        tvCompose = findViewById(R.id.mail_compose);
        showAdvance();
    }

    private void showAdvance() {
        tvComposeCC.setVisibility(isShowAdvance ? View.GONE : View.VISIBLE);
        tvComposeBCC.setVisibility(isShowAdvance ? View.GONE : View.VISIBLE);
        isShowAdvance = !isShowAdvance;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mail_compose_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mail_compose_menu_btn_send:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}