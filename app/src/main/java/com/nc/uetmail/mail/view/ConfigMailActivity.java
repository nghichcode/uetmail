package com.nc.uetmail.mail.view;

import android.content.DialogInterface;
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
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.UserModel;
import com.nc.uetmail.mail.database.models.UserModel.ConnectionType;
import com.nc.uetmail.mail.database.models.UserModel.MailProtocol;
import com.nc.uetmail.mail.database.repository.MailMasterRepository;
import com.nc.uetmail.mail.database.repository.UserRepository;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc;
import com.nc.uetmail.mail.utils.crypt.CryptorAesCbc.CryptData;

public class ConfigMailActivity extends AppCompatActivity {
    private boolean showAdvanced = true;
    private int configType;
    private UserRepository userRepository;
    private MailMasterRepository masterRepository;
    private String connectionType;

    private RelativeLayout rlUser;
    private RelativeLayout rlHost;
    private RelativeLayout rlPort;
    private RelativeLayout rlType;

    private TextView tvMail;
    private TextView tvUser;
    private TextView tvPass;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvPort;

    private EditText edMail;
    private EditText edUser;
    private EditText edPass;
    private EditText edHost;
    private EditText edType;
    private EditText edPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_activity_config_mail);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.colorDanger))
            );
        }
        Intent intent = getIntent();
        configType = intent.getIntExtra(
                HomeActivity.EXTRA_CONFIG_TYPE, HomeActivity.REQUEST_CONFIG_INB
        );
        userRepository = new UserRepository(this);
        masterRepository = new MailMasterRepository(this);

        rlUser = findViewById(R.id.mail_user_group);
        rlHost = findViewById(R.id.mail_host_group);
        rlPort = findViewById(R.id.mail_port_group);
        rlType = findViewById(R.id.mail_type_group);

        tvMail = findViewById(R.id.mail_email);
        tvUser = findViewById(R.id.mail_user);
        tvPass = findViewById(R.id.mail_pass);
        tvHost = findViewById(R.id.mail_host);
        tvType = findViewById(R.id.mail_type);
        tvPort = findViewById(R.id.mail_port);

        edMail = findViewById(R.id.mail_email_input);
        edUser = findViewById(R.id.mail_user_input);
        edPass = findViewById(R.id.mail_pass_input);
        edHost = findViewById(R.id.mail_host_input);
        edType = findViewById(R.id.mail_type_input);
        edPort = findViewById(R.id.mail_port_input);

        toggleAdvanced(null);
        edPort.setText("" + getDefaultPort(""));
        if (HomeActivity.REQUEST_CONFIG_INB == configType) {
            setTitle(R.string.mail_config_inbox);
            edHost.setHint(R.string.mail_config_host_hint);
        } else {
            setTitle(R.string.mail_config_outbox);
            edHost.setHint(R.string.mail_config_host_out_hint);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mail_config_menu, menu);
        if (HomeActivity.REQUEST_CONFIG_INB != configType) {
            menu.findItem(R.id.btn_next).setTitle(R.string.mail_finish);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_next:
                onNextPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == HomeActivity.REQUEST_CONFIG_OUB) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    public void showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        Menu menu = popup.getMenu();
        for (ConnectionType c : ConnectionType.values()) {
            menu.add(0, c.id, Menu.NONE, c.name);
        }
        popup.getMenuInflater().inflate(R.menu.mail_connection_type_menu, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for (ConnectionType c : ConnectionType.values()) {
                    if (c.id == item.getItemId()) {
                        edType.setText(c.name);
                        connectionType = c.name();
                        edPort.setText("" + getDefaultPort(connectionType));
                        break;
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    public void toggleAdvanced(View v) {
        rlUser.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlHost.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlType.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        rlPort.setVisibility(showAdvanced ? View.GONE : View.VISIBLE);
        showAdvanced = !showAdvanced;
    }

    public void onNextPressed() {
        int port = getDefaultPort(connectionType);
        String rawPass = edPass.getText().toString();
        try {
            port = Integer.parseInt(edPort.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        UserModel userModel = new UserModel(
                "",
                edMail.getText().toString(), edUser.getText().toString(),
                rawPass, edHost.getText().toString(),
                connectionType, port, "", true
        );

        if (configType == HomeActivity.REQUEST_CONFIG_INB) {
            userModel.protocol = MailProtocol.IMAP.name();
            userModel.incoming = true;
            if (!validate(userModel)) {return;}
            userRepository.insertFromRawPass(userModel);
            Intent configIntent = new Intent(this, ConfigMailActivity.class);
            configIntent.putExtra(HomeActivity.EXTRA_CONFIG_TYPE, HomeActivity.REQUEST_CONFIG_OUB);
            startActivityForResult(configIntent, HomeActivity.REQUEST_CONFIG_OUB);
        } else {
            userModel.protocol = MailProtocol.SMTP.name();
            userModel.incoming = false;
            if (!validate(userModel)) {return;}
            userRepository.insertFromRawPass(userModel);
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private boolean validate(UserModel userModel){
        if (!userModel.validate().isEmpty()) {
            new AlertDialog.Builder(this)
            .setTitle(R.string.mail_title_warning)
            .setMessage(R.string.mail_input_require_invalid)
            .show();
            return false;
        }
        return true;
    }

    private int getDefaultPort(String type) {
        return UserModel.getDefaultPort(
                (HomeActivity.REQUEST_CONFIG_OUB == configType) ?
                        MailProtocol.SMTP.name() :
                        MailProtocol.IMAP.name(),
                type
        );
    }
}