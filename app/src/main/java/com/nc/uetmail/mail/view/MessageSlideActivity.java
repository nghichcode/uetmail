package com.nc.uetmail.mail.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MailModel;
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
    private int fid;
    private ViewPager mPager;
    private MailViewModel mailVM;

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

        Intent intent = getIntent();
        int position = 0;
        if (intent.hasExtra(HomeActivity.EXTRA_MESSAGE_POS)) {
            position = intent.getIntExtra(HomeActivity.EXTRA_MESSAGE_POS, 0);
        }

        mPager = findViewById(R.id.mail_message_pager);
        final MessageSlidePagerAdapter msAdapter = new MessageSlidePagerAdapter(
            getSupportFragmentManager()
        );
        mPager.setAdapter(msAdapter);

        mailVM = ViewModelProviders.of(this).get(MailViewModel.class);
        final int positionTmp = position;
        mailVM.getLiveMailByActiveFolderId().observe(this, new Observer<List<MailModel>>() {
            @Override
            public void onChanged(@Nullable List<MailModel> models) {
                msAdapter.setModels(models);
                mPager.setCurrentItem(positionTmp);
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
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

        @Override
        public int getCount() {
            return models.size();
        }
    }
}
