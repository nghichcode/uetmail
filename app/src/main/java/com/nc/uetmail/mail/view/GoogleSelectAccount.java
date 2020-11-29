package com.nc.uetmail.mail.view;

import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.UserModel.MailProtocol;
import com.nc.uetmail.mail.database.repository.MailRepository;
import com.nc.uetmail.mail.database.repository.UserRepository;
import com.nc.uetmail.mail.utils.MailAndroidUtils;

import java.io.IOException;
import java.util.Arrays;

public class GoogleSelectAccount extends AppCompatActivity {
    public final int REQUEST_ACCOUNT_PICKER = 1;

    private String[] SCOPES = {
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_COMPOSE,
        GmailScopes.GMAIL_INSERT,
        GmailScopes.GMAIL_MODIFY,
        GmailScopes.GMAIL_READONLY,
        GmailScopes.MAIL_GOOGLE_COM
    };

    private GoogleAccountCredential mCredential;

    private Button btnConfig;
    private UserRepository userRepository;
    private MailRepository mailRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_google_select_account);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        userRepository = new UserRepository(this);
        mailRepository = new MailRepository(this);

        mCredential = GoogleAccountCredential
            .usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff());


        btnConfig = findViewById(R.id.mail_gg_config_again);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectActivity();
            }
        });

        startSelectActivity();
    }

    private void startSelectActivity() {
        if (mCredential == null) return;
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private void toggleLoading(boolean loading, Integer textId) {
        btnConfig.setClickable(!loading);
        if (textId != null) btnConfig.setText(textId);
        else btnConfig.setText(loading ? R.string.mail_loading : R.string.mail_try_again);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        toggleLoading(true, null);
                        new AccountInfoAsync(this, accountName).execute();
                        return;
                    }
                }
                MailAndroidUtils.showCtxToast(this, R.string.mail_add_google_pick_fail);
                break;
        }
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

    private static class AccountInfoAsync extends AsyncTask<Void, Void, Integer> {
        private GoogleSelectAccount activity;
        private String accountName;

        public AccountInfoAsync(final GoogleSelectAccount activity, final String accountName) {
            this.activity = activity;
            this.accountName = accountName;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
                Gmail service = new Gmail.Builder(
                    HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), activity.mCredential
                )
                    .setApplicationName(activity.getResources().getString(R.string.app_name))
                    .build();
                ListMessagesResponse res = service.users().messages().list(accountName).execute();
                if (res.getMessages().isEmpty()) {
                    return R.string.mail_add_google_pms_fail;
                } else return null;
            } catch (IOException e) {
                e.printStackTrace();
                return R.string.mail_add_google_fail;
            }
        }

        @Override
        protected void onPostExecute(Integer resid) {
            super.onPostExecute(resid);
            UserModel inbModel = new UserModel(
                MailProtocol.GMAIL.name(), accountName, accountName, "", "", "", 0, ""
                , true, true, 0, true
            );
            UserModel oubModel = new UserModel(
                MailProtocol.GMAIL.name(), accountName, accountName, "", "", "", 0, ""
                , true, false, 0, true
            );
            activity.userRepository.upsertGoogleAccount(inbModel, oubModel);
            activity.mailRepository.syncMail();
            activity.accountInfoResult(resid);
        }
    }

    private void accountInfoResult(Integer resid) {
        toggleLoading(false, resid != null ? null : R.string.mail_title_success);
        if (resid != null) {
            MailAndroidUtils.showCtxToast(this, resid);
            return;
        }
        setResult(RESULT_OK);
        finish();
    }

}