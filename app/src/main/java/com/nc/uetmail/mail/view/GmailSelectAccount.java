package com.nc.uetmail.mail.view;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.nc.uetmail.R;
import com.nc.uetmail.mail.async.AsyncTaskWithCallback;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GmailSelectAccount extends AppCompatActivity {
    public final int REQUEST_ACCOUNT_PICKER = 1;

    private String[] SCOPES = {
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_COMPOSE,
        GmailScopes.GMAIL_INSERT,
        GmailScopes.GMAIL_MODIFY,
        GmailScopes.GMAIL_READONLY,
        GmailScopes.MAIL_GOOGLE_COM
    };
    private String user;

    private GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_gmail_select_account);

        mCredential = GoogleAccountCredential
            .usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff());

        final GoogleAccountCredential gCredential = GoogleAccountCredential
            .usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES));
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

        Button btn = findViewById(R.id.gm_btn);
        TextView tv = findViewById(R.id.gm_tv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(":::Call2:");
                new AsyncTaskWithCallback.AsyncCallback(new AsyncTaskWithCallback.CallbackInterface() {
                    @Override
                    public void call() {
                        final HttpTransport HTTP_TRANSPORT;
                        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
                        try {
                            HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
//                            user = "ncmail@uetmail-1603445571162.iam.gserviceaccount.com";
//                            gCredential.setSelectedAccountName(user);
                            Gmail service = new Gmail
                                .Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                                .setApplicationName(getResources().getString(R.string.app_name))
                                .build();
                            ListLabelsResponse listResponse =
                                service.users().labels().list(user).execute();
                            List<Label> labels = listResponse.getLabels();
                            if (labels.isEmpty()) {
                                System.out.println("No labels found.");
                            } else {
                                System.out.println("Labels:");
                                for (Label label : labels) {
                                    System.out.printf("- %s\n", label.getName());
                                }
                            }
                            ListMessagesResponse listMessagesResponse = service
                                .users().messages().list(user)
                                .setLabelIds(Arrays.asList(new String[]{"INBOX"})).execute();
                            List<Message> messages = listMessagesResponse.getMessages();
                            if (messages.isEmpty()) {
                                System.out.println("No labels found.");
                            } else {
                                System.out.println("Labels:");
                                for (Message message : messages) {
                                    System.out.printf("- %s\n", message.getId());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//        String accountName = "me";
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    mCredential.setSelectedAccountName(accountName);
                    user = accountName;
                    System.out.println(accountName);
                }
                break;
        }
    }
}