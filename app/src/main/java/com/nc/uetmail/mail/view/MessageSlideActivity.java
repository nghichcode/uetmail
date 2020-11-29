package com.nc.uetmail.mail.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.view.ComposeMailActivity.ComposeType;
import com.nc.uetmail.mail.viewmodel.MailViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class MessageSlideActivity extends AppCompatActivity {
    public static final String EXTRA_COMPOSE_MSID = "com.nc.uetmail.mail.EXTRA_COMPOSE_MSID";
    public static final String EXTRA_COMPOSE_TYPE = "com.nc.uetmail.mail.EXTRA_COMPOSE_TYPE";
    public static final int REQUEST_COMPOSE = 1;

    private ViewPager mPager;
    private MailViewModel mailVM;
    private MessageSlidePagerAdapter msAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_message_slide_activity);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mailVM = ViewModelProviders.of(this).get(MailViewModel.class);
        Intent intent = getIntent();
        int position = 0;
        if (intent.hasExtra(HomeActivity.EXTRA_MESSAGE_POS)) {
            position = intent.getIntExtra(HomeActivity.EXTRA_MESSAGE_POS, 0);
        }
        int messageId = 0;
        if (intent.hasExtra(HomeActivity.EXTRA_MESSAGE_ID)) {
            messageId = intent.getIntExtra(HomeActivity.EXTRA_MESSAGE_ID, 0);
        }
        final int positionTmp = position;

        mPager = findViewById(R.id.mail_message_pager);
        msAdapter = new MessageSlidePagerAdapter(
            getSupportFragmentManager()
        );
        mPager.setAdapter(msAdapter);

        if (positionTmp < 0) {
            mailVM.getByMessageId(messageId).observe(this, new Observer<MailModel>() {
                @Override
                public void onChanged(@Nullable MailModel model) {
                    if (model == null) return;
                    ArrayList<MailModel> models = new ArrayList<>();
                    models.add(model);
                    msAdapter.setModels(models);
                    mPager.setCurrentItem(0);
                }
            });
        } else {
            mailVM.getLiveMailByActiveFolderId().observe(this, new Observer<List<MailModel>>() {
                @Override
                public void onChanged(@Nullable List<MailModel> models) {
                    if (models == null || positionTmp < 0) return;
                    msAdapter.setModels(models);
                    mPager.setCurrentItem(positionTmp);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mail_message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mail_message_item_reply:
                showComposeMailActivity(ComposeType.REPLY);
                break;
            case R.id.mail_message_item_reply_all:
                showComposeMailActivity(ComposeType.REPLY_ALL);
                break;
            case R.id.mail_message_item_forward:
                showComposeMailActivity(ComposeType.FORWARD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComposeMailActivity(ComposeType type) {
        Intent intent = new Intent(this, ComposeMailActivity.class);
        MailModel currentMail = msAdapter.getMail(mPager.getCurrentItem());
        if (currentMail != null)
            intent.putExtra(EXTRA_COMPOSE_MSID, currentMail.id);
        else intent.putExtra(EXTRA_COMPOSE_MSID, 0);
        intent.putExtra(EXTRA_COMPOSE_TYPE, type.name());
        startActivityForResult(intent, REQUEST_COMPOSE);
    }

    private class MessageSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<MailModel> models = new ArrayList<>();

        public MessageSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setModels(final List<MailModel> models) {
            this.models = models;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return new MessageSlideFragment(models.get(position));
        }

        public MailModel getMail(int position) {
            return models.get(position);
        }

        @Override
        public int getCount() {
            return models.size();
        }
    }
}
