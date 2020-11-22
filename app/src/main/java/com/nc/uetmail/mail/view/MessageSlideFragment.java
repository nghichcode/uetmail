package com.nc.uetmail.mail.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.adapters.AttachRecyclerAdapter;
import com.nc.uetmail.mail.database.models.AttachmentModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.utils.MailAndroidUtils;
import com.nc.uetmail.mail.viewmodel.AttachViewModel;
import com.nc.uetmail.mail.viewmodel.MailViewModel;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageSlideFragment extends Fragment {
    private MailModel model;

    private AttachRecyclerAdapter attachAdapter;
    private AttachViewModel attachVM;

    private TextView tvSubject;
    private TextView ivIconLetter;
    private TextView tvFrom;
    private TextView tvSentDate;
    private TextView tvTo;
    private LinearLayout llCc;
    private TextView tvCc;
    private RecyclerView rvListAttach;
    private WebView wvMessage;
    private TextView tvMessage;

    public MessageSlideFragment(MailModel model) {
        this.model = model;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
            R.layout.mail_message_slide_page, container, false
        );

        tvSubject = rootView.findViewById(R.id.mail_message_subject);
        ivIconLetter = rootView.findViewById(R.id.mail_message_icon_letter);
        tvFrom = rootView.findViewById(R.id.mail_message_from);
        tvSentDate = rootView.findViewById(R.id.mail_message_sent_date);
        tvTo = rootView.findViewById(R.id.mail_message_to);
        llCc = rootView.findViewById(R.id.mail_message_cc_group);
        tvCc = rootView.findViewById(R.id.mail_message_cc);
        wvMessage = rootView.findViewById(R.id.mail_message_wv);
        tvMessage = rootView.findViewById(R.id.mail_message_tv);

        WebSettings webSettings = wvMessage.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        rvListAttach = rootView.findViewById(R.id.mail_message_list_attach);
        rvListAttach.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListAttach.setHasFixedSize(true);
        rvListAttach.addItemDecoration(
            new DividerItemDecoration(rvListAttach.getContext(),
                DividerItemDecoration.VERTICAL)
        );

        attachAdapter = new AttachRecyclerAdapter();
        attachAdapter.setOnItemClickListener(new AttachRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AttachmentModel model) {
                openAttachFile(model);
            }
        });
        rvListAttach.setAdapter(attachAdapter);

        attachVM = ViewModelProviders.of(this).get(AttachViewModel.class);
        attachVM.getByMessageId(model.id).observe(this,
            new Observer<List<AttachmentModel>>() {
                @Override
                public void onChanged(@Nullable List<AttachmentModel> models) {
                    attachAdapter.setAttachments(models);
                }
            });
        loadMessage(model);

        return rootView;
    }

    private void openAttachFile(AttachmentModel model) {
        if (model == null) return;
        Uri uri = Uri.fromFile(new File(model.path));
        String mimeType;

        Intent it = new Intent(Intent.ACTION_VIEW);
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            mimeType = getContext().getContentResolver().getType(uri);
        } else {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString()).toLowerCase()
            );
        }
        if (mimeType == null || "".equals(mimeType.trim())) {
            new AlertDialog.Builder(getContext())
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
        llCc.setVisibility(model.mail_cc.isEmpty() ? View.GONE : View.VISIBLE);
        tvCc.setText(MailMessage.toAddressString(model.mail_cc));

        if ("".equals(model.mail_content_html)) {
            tvMessage.setVisibility(View.VISIBLE);
            wvMessage.setVisibility(View.GONE);
            tvMessage.setText(model.mail_content_txt);
        } else {
            tvMessage.setVisibility(View.GONE);
            wvMessage.setVisibility(View.VISIBLE);
            wvMessage.loadDataWithBaseURL(
                MailAndroidUtils.ROOT_URI + model.attachments_folder + File.separator,
                model.mail_content_html, "text/html", "utf-8",
                null
            );
        }
    }

}
