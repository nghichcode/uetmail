package com.nc.uetmail.mail.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.adapters.AttachRecyclerAdapter;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.viewmodel.AttachViewModel;
import com.nc.uetmail.mail.viewmodel.MailViewModel;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageActivity extends AppCompatActivity {

    private AttachRecyclerAdapter attachAdapter;
    private AttachViewModel attachVM;
    private MailViewModel mailVM;

    private TextView tvSubject;
    private TextView ivIconLetter;
    private TextView tvFrom;
    private TextView tvSentDate;
    private TextView tvTo;
    private TextView tvCc;
    private RecyclerView rvListAttach;
    private WebView wvMessage;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_message_activity);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int msid = 0;
        if (intent.hasExtra(HomeActivity.EXTRA_MESSAGE_ID)) {
            msid = intent.getIntExtra(HomeActivity.EXTRA_MESSAGE_ID, 0);
        }

        tvSubject = findViewById(R.id.mail_message_subject);
        ivIconLetter = findViewById(R.id.mail_message_icon_letter);
        tvFrom = findViewById(R.id.mail_message_from);
        tvSentDate = findViewById(R.id.mail_message_sent_date);
        tvTo = findViewById(R.id.mail_message_to);
        tvCc = findViewById(R.id.mail_message_cc);
        wvMessage = findViewById(R.id.mail_message_wv);
        tvMessage = findViewById(R.id.mail_message_tv);

        WebSettings webSettings = wvMessage.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        wvMessage.setWebViewClient(new WebViewClient());
        wvMessage.setWebChromeClient(new WebChromeClient());

//        this.webViewClient = new EmailWebViewClient();
//        this.webChromeClient = new EmailWebChromeClient();
//        setWebViewClient(this.webViewClient);
//        setWebChromeClient(this.webChromeClient);
//        this.c = new NativeToJsMessageQueue(this);
//        this.b = new EmailJsAPI(this);
//        addJavascriptInterface(this.b, a);
//        this.e = new WeakHashMap<>();
//
//        webSettings.setJavaScriptEnabled(true);
//
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);
//        webSettings.setAllowFileAccess(true);
//
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setLoadsImagesAutomatically(true);

        rvListAttach = findViewById(R.id.mail_message_list_attach);
        rvListAttach.setLayoutManager(new LinearLayoutManager(this));
        rvListAttach.setHasFixedSize(true);

        attachAdapter = new AttachRecyclerAdapter();
        attachAdapter.setOnItemClickListener(new AttachRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AttachmentModel model) {
                openAttachFile(model);
            }
        });
        rvListAttach.setAdapter(attachAdapter);

        attachVM = ViewModelProviders.of(this).get(AttachViewModel.class);
        attachVM.getByMessageId(msid).observe(this, new Observer<List<AttachmentModel>>() {
            @Override
            public void onChanged(@Nullable List<AttachmentModel> models) {
                attachAdapter.setAttachments(models);
            }
        });

        mailVM = ViewModelProviders.of(this).get(MailViewModel.class);
        mailVM.getByMessageId(msid).observe(this, new Observer<MailModel>() {
            @Override
            public void onChanged(@Nullable MailModel model) {
                loadMessage(model);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mail_config_menu_btn_next:
//                onNextPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAttachFile(AttachmentModel model) {
        if (model == null) return;
        Uri uri = Uri.fromFile(new File(model.path));
        String mimeType;

        Intent it = new Intent(Intent.ACTION_VIEW);
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            mimeType = getContentResolver().getType(uri);
        } else {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString()).toLowerCase()
            );
        }
        if (mimeType == null || "".equals(mimeType.trim())) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.mail_title_danger)
                .setMessage(R.string.mail_attach_invalid)
                .show();
        }
        it.setDataAndType(uri, mimeType);
        it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(it);
    }

    private void loadMessage(MailModel model) {
        if (model == null) return;

        tvSentDate.setText(model.getFormatSentDate());
        model.nullToEmpty();
        tvSubject.setText(model.mail_subject);
        ivIconLetter.setText(model.getFirstUserLetter());
        tvFrom.setText(MailMessage.toAddressString(model.mail_from));
        tvTo.setText(MailMessage.toAddressString(model.mail_to));
        tvCc.setText(MailMessage.toAddressString(model.mail_cc));

        WebSettings webSettings = wvMessage.getSettings();
        if (model.mail_has_attachment) {
            webSettings.setAppCachePath(model.attachments_folder);
        }


        if ("".equals(model.mail_content_html)) {
            tvMessage.setVisibility(View.VISIBLE);
            wvMessage.setVisibility(View.GONE);
            tvMessage.setText(model.mail_content_txt);
        } else {
            tvMessage.setVisibility(View.GONE);
            wvMessage.setVisibility(View.VISIBLE);
            wvMessage.loadData(
                model.mail_content_html, "text/html; charset=utf-8", "utf-8"
            );
        }
    }


}