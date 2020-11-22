package com.nc.uetmail.mail.view;

import android.graphics.drawable.ColorDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.nc.uetmail.mail.viewmodel.MailViewModel;

import java.util.List;

public class ComposeMailActivity extends AppCompatActivity {
    private boolean isShowAdvance = true;
    private MailViewModel mailViewModel;

    private ImageButton btnComposeExpand;
    private TextView tvComposeTo;
    private TextView tvComposeCC;
    private TextView tvComposeBCC;
    private TextView tvComposeSubject;
    private TextView tvComposeTxt;


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
        tvComposeTxt = findViewById(R.id.mail_compose_txt);
        showAdvance();
        mailViewModel = ViewModelProviders.of(this).get(MailViewModel.class);
    }

    private void showAdvance() {
        tvComposeCC.setVisibility(isShowAdvance ? View.GONE : View.VISIBLE);
        tvComposeBCC.setVisibility(isShowAdvance ? View.GONE : View.VISIBLE);
        btnComposeExpand.setImageResource(
            isShowAdvance ? R.drawable.ic_expand_more : R.drawable.ic_expand_less
        );
        isShowAdvance = !isShowAdvance;
    }

    private void sendMail() {
        MailModel model = new MailModel();
        model.mail_to = tvComposeTo.getText().toString();
        model.mail_cc = tvComposeCC.getText().toString();
        model.mail_bcc = tvComposeBCC.getText().toString();
        model.mail_subject = tvComposeSubject.getText().toString();
        model.mail_content_txt = tvComposeTxt.getText().toString();
        if (model.validate().size()>0) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.mail_title_warning)
                .setMessage(R.string.mail_input_require_invalid)
                .show();
            return;
        }
        mailViewModel.sendMail(model);

        MailAndroidUtils.showCtxToast(this, R.string.mail_sending);
        setResult(RESULT_OK);
        finish();
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
                sendMail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}