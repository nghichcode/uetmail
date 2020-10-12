package com.nc.uetmail.mail.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.async.InitDB;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.viewmodel.MessageViewModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_CONFIG_TYPE = "com.nc.uetmail.mail.EXTRA_CONFIG_TYPE";
    public static final int REQUEST_CONFIG_INB = 1;
    public static final int REQUEST_CONFIG_OUB = 2;

    private MessageViewModel messageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_activity_home);

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getAllMessages().observe(this, new Observer<List<MailModel>>() {
            @Override
            public void onChanged(@Nullable List<MailModel> mailModels) {
//                adapter.setMessages(mailModels);
            }
        });
    }

    public void showConfig(View v) {
        Intent configIntent = new Intent(this, ConfigMailActivity.class);
        configIntent.putExtra(EXTRA_CONFIG_TYPE, REQUEST_CONFIG_INB);
        startActivityForResult(configIntent, REQUEST_CONFIG_INB);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CONFIG_INB) {
//            Intent configIntent = new Intent(this, ConfigMailActivity.class);
//            configIntent.putExtra(EXTRA_CONFIG_TYPE, REQUEST_CONFIG_OUB);
//            startActivityForResult(configIntent, REQUEST_CONFIG_OUB);
//        }
    }
}