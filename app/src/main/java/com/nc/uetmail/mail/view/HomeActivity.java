package com.nc.uetmail.mail.view;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.adapters.FolderRecyclerAdapter;
import com.nc.uetmail.mail.adapters.MailRecyclerAdapter;
import com.nc.uetmail.mail.adapters.UserMailRecyclerAdapter;
import com.nc.uetmail.mail.broadcast_receiver.SyncMailReceiver;
import com.nc.uetmail.mail.database.models.FolderModel;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.utils.touch_helper.HomeTouchCallback;
import com.nc.uetmail.mail.utils.touch_helper.HomeTouchCallback.SwipeInterface;
import com.nc.uetmail.mail.viewmodel.FolderViewModel;
import com.nc.uetmail.mail.viewmodel.MasterViewModel;
import com.nc.uetmail.mail.viewmodel.MailViewModel;
import com.nc.uetmail.mail.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    public static final String EXTRA_CONFIG_UID = "com.nc.uetmail.mail.EXTRA_CONFIG_UID";
    public static final String EXTRA_MESSAGE_ID = "com.nc.uetmail.mail.EXTRA_MESSAGE_ID";
    public static final String EXTRA_MESSAGE_POS = "com.nc.uetmail.mail.EXTRA_MESSAGE_POS";
    public static final int REQUEST_CONFIG = 1;
    public static final int REQUEST_CONFIG_GG = 2;
    public static final int REQUEST_COMPOSE = 3;
    public static final int REQUEST_MESSAGE = 4;
    public static final int REQUEST_SEARCH = 5;
    public static final int REQUEST_CONFIG_PM = 6;

    private MasterViewModel masterViewModel;
    private FolderViewModel folderViewModel;
    private MailViewModel mailViewModel;
    private UserViewModel userViewModel;
    private FolderRecyclerAdapter folderAdapter;
    private MailRecyclerAdapter messageAdapter;
    private UserMailRecyclerAdapter userMailAdapter;
    private RecyclerView rvListEmail;
    private RecyclerView rvListFolder;

    private LinearLayout llFolderGroup;

    private DrawerLayout drawer;
    private TextView tvNavUser;
    private TextView tvNavEmail;
    private ImageButton btnNavExpand;
    private LinearLayout btnNavUserGroup;

    private AlertDialog alertDialog;
    private HomeTouchCallback mailTouch;
    private HomeTouchCallback userTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mail_toolbar);
        setSupportActionBar(toolbar);
        initTouch();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeActivity(view);
            }
        });

        // Folder Group
        llFolderGroup = findViewById(R.id.mail_row_folder_back_group);
        llFolderGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFolder(true);
                llFolderGroup.setVisibility(View.GONE);
            }
        });
        drawer = findViewById(R.id.mail_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.mail_nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        // Init recycle view
        View headerView = navigationView.getHeaderView(0);
        rvListEmail = findViewById(R.id.mail_home_list_email);
        rvListFolder = findViewById(R.id.mail_home_list_folder);
        // Init Message
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
                showMessageActivity(model, position);
            }
        });
        new ItemTouchHelper(mailTouch).attachToRecyclerView(rvListEmail);
        rvListEmail.setAdapter(messageAdapter);

        // Init Folder
        rvListFolder.setLayoutManager(new LinearLayoutManager(this));
        rvListFolder.setHasFixedSize(true);
        rvListFolder.addItemDecoration(
            new DividerItemDecoration(rvListFolder.getContext(),
                DividerItemDecoration.VERTICAL)
        );

        folderAdapter = new FolderRecyclerAdapter();
        folderAdapter.setOnItemClickListener(new FolderRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FolderModel model) {
                masterViewModel.setActiveFolderId(model.id);
                showFolder(false);
                llFolderGroup.setVisibility(View.VISIBLE);
            }
        });
        rvListFolder.setAdapter(folderAdapter);
        // Init VM
        masterViewModel = ViewModelProviders.of(this).get(MasterViewModel.class);
        masterViewModel.setActiveFolder(FolderModel.FolderType.INBOX.name());
        showFolder(false);
        llFolderGroup.setVisibility(View.GONE);
        folderViewModel = ViewModelProviders.of(this).get(FolderViewModel.class);
        folderViewModel.getActiveFolders(FolderModel.FolderType.OTHER.name()).observe(this,
            new Observer<List<FolderModel>>() {
                @Override
                public void onChanged(List<FolderModel> folderModels) {
                    folderAdapter.setModels(folderModels);
                }
            });
        mailViewModel = ViewModelProviders.of(this).get(MailViewModel.class);
        mailViewModel.getLiveMailByActiveFolderId().observe(this, new Observer<List<MailModel>>() {
            @Override
            public void onChanged(@Nullable List<MailModel> mailModels) {
                messageAdapter.setMessages(mailModels);
            }
        });

        tvNavUser = headerView.findViewById(R.id.mail_home_nav_header_user);
        tvNavEmail = headerView.findViewById(R.id.mail_home_nav_header_email);
        btnNavExpand = headerView.findViewById(R.id.mail_home_nav_header_expand);
        btnNavUserGroup = headerView.findViewById(R.id.mail_home_nav_header_user_group);
        btnNavExpand.setOnClickListener(onClickExpand);
        btnNavUserGroup.setOnClickListener(onClickExpand);

        userMailAdapter = new UserMailRecyclerAdapter();
        userMailAdapter.setOnItemClickListener(new UserMailRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserModel model) {
                masterViewModel.setActiveUser(model);
                if (alertDialog != null) alertDialog.hide();
            }
        });
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.mail_select_user);
        final View customView = getLayoutInflater().inflate(R.layout.mail_home_select_user, null);
        RecyclerView rvSelectUser = customView.findViewById(R.id.mail_home_select_user_list);
        Button btnAddUser = customView.findViewById(R.id.mail_home_add_user_btn);
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigActivity(0);
                if (alertDialog != null) alertDialog.hide();
            }
        });
        LinearLayout llAddGGUser = customView.findViewById(R.id.mail_home_add_gg_user_btn);
        llAddGGUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGGConfigActivity();
                if (alertDialog != null) alertDialog.hide();
            }
        });
        rvSelectUser.setLayoutManager(new LinearLayoutManager(this));
        rvSelectUser.setHasFixedSize(true);
        rvSelectUser.addItemDecoration(
            new DividerItemDecoration(rvListEmail.getContext(),
                DividerItemDecoration.VERTICAL)
        );
        new ItemTouchHelper(userTouch).attachToRecyclerView(rvSelectUser);
        rvSelectUser.setAdapter(userMailAdapter);
        alertBuilder.setView(customView);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUsers().observe(this, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(@Nullable List<UserModel> models) {
                userMailAdapter.setUsers(models);
            }
        });
        userViewModel.getActiveInbUser().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(@Nullable UserModel userModel) {
                if (userModel == null) {
                    tvNavUser.setText(R.string.mail_row_empty_user);
                    tvNavEmail.setText(R.string.mail_row_empty_user_hint);
                } else {
                    userModel.nullToEmpty();
                    tvNavUser.setText(userModel.user);
                    tvNavEmail.setText(userModel.email);
                }
            }
        });
        alertDialog = alertBuilder.create();

        registerPermission();
    }

    public void registerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(new SyncMailReceiver(), filter);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, REQUEST_CONFIG_PM);
            }
        }
    }

    public void showConfigActivity(int uid) {
        Intent configIntent = new Intent(this, ConfigMailActivity.class);
        if (uid < 0) return;
        if (uid > 0) configIntent.putExtra(EXTRA_CONFIG_UID, uid);
        startActivityForResult(configIntent, REQUEST_CONFIG);
    }

    public void showGGConfigActivity() {
        Intent configIntent = new Intent(this, GoogleSelectAccount.class);
        startActivityForResult(configIntent, REQUEST_CONFIG_GG);
    }

    public void showComposeActivity(View v) {
        Intent configIntent = new Intent(this, ComposeMailActivity.class);
        startActivityForResult(configIntent, REQUEST_COMPOSE);
    }

    public void showMessageActivity(MailModel model, int position) {
        mailViewModel.seenMail(model);
        Intent intent = new Intent(this, MessageSlideActivity.class);
        intent.putExtra(EXTRA_MESSAGE_ID, model.id);
        intent.putExtra(EXTRA_MESSAGE_POS, position);
        startActivityForResult(intent, REQUEST_MESSAGE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CONFIG) {
//        }
    }

    private void initTouch() {
        mailTouch = new HomeTouchCallback(this, new SwipeInterface() {
            @Override
            public void onSwiped(@NonNull ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    showToast(getResources().getString(R.string.mail_trash));
                    mailViewModel.archiveMail(messageAdapter.getMessageAt(viewHolder.getAdapterPosition()), true);
                } else {
                    showToast(getResources().getString(R.string.mail_deleted));
                    mailViewModel.archiveMail(messageAdapter.getMessageAt(viewHolder.getAdapterPosition()), false);
                }
                messageAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        },
            getResources().getString(R.string.mail_delete),
            getResources().getString(R.string.mail_trash),
            0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);

        userTouch = new HomeTouchCallback(this, new SwipeInterface() {
            @Override
            public void onSwiped(@NonNull ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    showToast(getResources().getString(R.string.mail_edit));
                    UserModel u = userMailAdapter.getUserAt(viewHolder.getAdapterPosition());
                    showConfigActivity(u != null ? u.id : -1);
                } else {
                    showToast(getResources().getString(R.string.mail_deleted));
                    userViewModel.delete(userMailAdapter.getUserAt(viewHolder.getAdapterPosition()));
                }
                userMailAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        },
            getResources().getString(R.string.mail_delete),
            getResources().getString(R.string.mail_edit),
            0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }

    private void showFolder(boolean showFolder) {
        rvListFolder.setVisibility(showFolder ? View.VISIBLE : View.GONE);
        rvListEmail.setVisibility(showFolder ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean showFolder = false;
        switch (item.getItemId()) {
            case R.id.mail_home_nav_inbox:
                masterViewModel.setActiveFolder(FolderModel.FolderType.INBOX.name());
                break;
            case R.id.mail_home_nav_archive:
                masterViewModel.setActiveFolder(FolderModel.FolderType.ARCHIVE.name());
                break;
            case R.id.mail_home_nav_drafts:
                masterViewModel.setActiveFolder(FolderModel.FolderType.DRAFTS.name());
                break;
            case R.id.mail_home_nav_sent:
                masterViewModel.setActiveFolder(FolderModel.FolderType.SENT.name());
                break;
            case R.id.mail_home_nav_trash:
                masterViewModel.setActiveFolder(FolderModel.FolderType.TRASH.name());
                break;
            case R.id.mail_home_nav_spam:
                masterViewModel.setActiveFolder(FolderModel.FolderType.SPAM.name());
                break;
            case R.id.mail_home_nav_other:
                masterViewModel.setActiveFolderId(-1);
                showFolder = true;
                break;
            case R.id.mail_home_nav_about:
                new AlertDialog.Builder(this)
                    .setTitle(R.string.mail_about_title)
                    .setMessage(R.string.mail_about_body)
                    .create().show();
                break;
        }
        showFolder(showFolder);
        llFolderGroup.setVisibility(View.GONE);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private View.OnClickListener onClickExpand = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawer.closeDrawer(GravityCompat.START);
            alertDialog.show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mail_home_menu, menu);
//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(getResources().getColor(R.color.colorWhite),
//                    PorterDuff.Mode.SRC_ATOP);
//            }
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mail_home_menu_btn_sync:
                mailViewModel.syncMail(true);
                showToast(getResources().getString(R.string.mail_sync_start));
                break;
            case R.id.mail_home_menu_btn_search:
                Intent intent = new Intent(this, SearchMailActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH);
//                mailViewModel.deleteEmptyActiveUserMs();
//                showToast(getResources().getString(R.string.mail_sync_start));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}