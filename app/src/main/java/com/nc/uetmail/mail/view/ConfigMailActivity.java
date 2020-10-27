package com.nc.uetmail.mail.view;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.UserModel.ConnectionType;
import com.nc.uetmail.mail.database.models.UserModel.MailProtocol;
import com.nc.uetmail.mail.database.repository.UserRepository;

import java.util.List;

public class ConfigMailActivity extends AppCompatActivity {

    private boolean showAdvanced = true;
    private UserRepository userRepository;

    private UserModel inbModel;
    private UserModel oubModel;

    private RelativeLayout rlInUser;
    // Pass RQ
    private RelativeLayout rlInHost;
    private RelativeLayout rlInType;
    private RelativeLayout rlInPort;
    private RelativeLayout rlOuUser;
    private RelativeLayout rlOuPass;
    private RelativeLayout rlOuHost;
    private RelativeLayout rlOuType;
    private RelativeLayout rlOuPort;

    private EditText edMail;
    private EditText edInUser;
    private EditText edInPass;
    private EditText edInHost;
    private EditText edInType;
    private EditText edInPort;
    private EditText edOuUser;
    private EditText edOuPass;
    private EditText edOuHost;
    private EditText edOuType;
    private EditText edOuPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_config_mail_activity);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        userRepository = new UserRepository(this);
        inbModel = new UserModel();
        oubModel = new UserModel();

        rlInUser = findViewById(R.id.mail_in_user_group);
        // Pass RQ
        rlInHost = findViewById(R.id.mail_in_host_group);
        rlInPort = findViewById(R.id.mail_in_port_group);
        rlInType = findViewById(R.id.mail_in_type_group);
        rlOuUser = findViewById(R.id.mail_ou_user_group);
        rlOuPass = findViewById(R.id.mail_ou_pass_group);
        rlOuHost = findViewById(R.id.mail_ou_host_group);
        rlOuPort = findViewById(R.id.mail_ou_port_group);
        rlOuType = findViewById(R.id.mail_ou_type_group);

        edMail = findViewById(R.id.mail_email_input);
        edInUser = findViewById(R.id.mail_in_user_input);
        edInPass = findViewById(R.id.mail_in_pass_input);
        edInHost = findViewById(R.id.mail_in_host_input);
        edInType = findViewById(R.id.mail_in_type_input);
        edInPort = findViewById(R.id.mail_in_port_input);
        edOuUser = findViewById(R.id.mail_ou_user_input);
        edOuPass = findViewById(R.id.mail_ou_pass_input);
        edOuHost = findViewById(R.id.mail_ou_host_input);
        edOuType = findViewById(R.id.mail_ou_type_input);
        edOuPort = findViewById(R.id.mail_ou_port_input);

        toggleAdvanced(null);
        edInType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeMenu(v, true);
            }
        });
        edOuType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeMenu(v, false);
            }
        });
        edInPort.setText("" + UserModel.getDefaultPort(MailProtocol.IMAP.name(), inbModel.type));
        edOuPort.setText("" + UserModel.getDefaultPort(MailProtocol.SMTP.name(), oubModel.type));

        int iuid = 0;
        Intent intent = getIntent();
        if (intent.hasExtra(HomeActivity.EXTRA_CONFIG_UID)) {
            iuid = intent.getIntExtra(HomeActivity.EXTRA_CONFIG_UID, 0);
        }

        if (iuid > 0)
            userRepository.getUsersByIdOrTargetId(iuid).observe(this,
                new Observer<List<UserModel>>() {
                    @Override
                    public void onChanged(@Nullable List<UserModel> userModels) {
                        if (userModels.size() != 2) return;
                        UserModel inbModelTmp = userRepository.decryptUser(userModels.get(0));
                        UserModel oubModelTmp = userRepository.decryptUser(userModels.get(1));
                        if (inbModelTmp.id != oubModelTmp.target_id) {
                            inbModel = oubModelTmp;
                            oubModel = inbModelTmp;
                        } else {
                            inbModel = inbModelTmp;
                            oubModel = oubModelTmp;
                        }
                        edMail.setText(inbModel.email);
                        edInUser.setText(inbModel.user);
                        edInPass.setText(inbModel.pass);
                        edInHost.setText(inbModel.hostname);
                        edInType.setText(inbModel.type);
                        edInPort.setText(inbModel.port);
                        edOuUser.setText(oubModel.user);
                        edOuPass.setText(oubModel.pass);
                        edOuHost.setText(oubModel.hostname);
                        edOuType.setText(oubModel.type);
                        edOuPort.setText(oubModel.port);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mail_config_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mail_config_menu_btn_next:
                onNextPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTypeMenu(View anchor, final boolean incoming) {
        PopupMenu popup = new PopupMenu(this, anchor);
        Menu menu = popup.getMenu();
        for (ConnectionType c : ConnectionType.values()) {
            menu.add(0, c.id, Menu.NONE, c.name);
        }
        popup.getMenuInflater().inflate(R.menu.mail_config_connection_type_menu, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for (ConnectionType c : ConnectionType.values()) {
                    if (c.id == item.getItemId()) {
                        if (incoming) {
                            edInType.setText(c.name);
                            edInPort.setText("" + UserModel.getDefaultPort(MailProtocol.IMAP.name(), c.name()));
                            break;
                        } else {
                            edOuType.setText(c.name);
                            edOuPort.setText("" + UserModel.getDefaultPort(MailProtocol.SMTP.name(), c.name()));
                            break;
                        }
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    public void toggleAdvanced(View v) {
        rlInUser.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        // Pass RQ
        rlInHost.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlInPort.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlInType.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlOuUser.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlOuPass.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlOuHost.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlOuPort.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlOuType.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);

        showAdvanced = !showAdvanced;
    }

    public void onNextPressed() {
        inbModel.email = oubModel.email = edMail.getText().toString();
        if ("".equals(inbModel.email) || inbModel.email.split("@").length != 2) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.mail_title_warning)
                .setMessage(R.string.mail_input_require_invalid)
                .show();
        }
        inbModel.sync = oubModel.sync = true;

        inbModel.protocol = MailProtocol.IMAP.name();
        inbModel.user = edInUser.getText().toString();
        inbModel.pass = edInPass.getText().toString();
        inbModel.hostname = edInHost.getText().toString();
        try {
            inbModel.port = Integer.parseInt(edInPort.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        inbModel.incoming = true;

        oubModel.protocol = MailProtocol.SMTP.name();
        oubModel.user = edOuUser.getText().toString();
        oubModel.pass = edOuPass.getText().toString();
        oubModel.hostname = edOuHost.getText().toString();
        try {
            oubModel.port = Integer.parseInt(edOuPort.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        oubModel.incoming = false;

        if ("".equals(oubModel.user)) oubModel.user = inbModel.user;
        if ("".equals(oubModel.pass)) oubModel.pass = inbModel.pass;

        if (validate(inbModel) && validate(oubModel)) {
            userRepository.insertFromRawPass(inbModel, oubModel);
            final Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean validate(UserModel userModel) {
        if (!userModel.validate().isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.mail_title_warning)
                .setMessage(R.string.mail_input_require_invalid)
                .show();
            return false;
        }
        return true;
    }

}