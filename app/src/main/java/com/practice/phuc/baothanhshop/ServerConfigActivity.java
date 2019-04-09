package com.practice.phuc.baothanhshop;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.baothanhshop.Utils.OkHttpUtil;

import java.util.Objects;

public class ServerConfigActivity extends AppCompatActivity {

    private EditText mPort;
    private EditText mServerAddr;
    private TextView mPreviewServerConfig;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);
        Toolbar toolbar = findViewById(R.id.toolbar_server_config);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreviewServerConfig = findViewById(R.id.tv_preview_server_config);
        mPort = findViewById(R.id.et_port);
        mServerAddr = findViewById(R.id.et_server_address);
        Button mConfigBtn = findViewById(R.id.btn_config);
        Button mRestoreDefault = findViewById(R.id.btn_reset_server_config);
        mConfigBtn.setOnClickListener(configBtnClickListener);
        mRestoreDefault.setOnClickListener(restoreDefault);

        mSharedPreferences = getSharedPreferences(OkHttpUtil.SHARED_PRE_SERVER, MODE_PRIVATE);
        String oldPort = OkHttpUtil.getServerPort(this);
        String oldServerAddr = OkHttpUtil.getServerAddress(this);
        String serverConfig = "http://" + oldServerAddr + ":" + oldPort + "/api/{controller}/{param}";
        mPort.append(oldPort);
        mServerAddr.append(oldServerAddr);
        mPreviewServerConfig.setText(serverConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    View.OnClickListener configBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String port = mPort.getText().toString();
            String serverAddr = mServerAddr.getText().toString();

            if (!port.isEmpty() && !serverAddr.isEmpty()) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(OkHttpUtil.SHARED_PRE_KEY_SERVER_PORT, port);
                editor.putString(OkHttpUtil.SHARED_PRE_KEY_SERVER_ADDRESS, serverAddr);
                editor.apply();

                Toast.makeText(ServerConfigActivity.this, "Đã lưu cấu hình server", Toast.LENGTH_SHORT)
                .show();

                String serverConfig = "http://" + serverAddr + ":" + port + "/api/{controller}/{param}";
                mPreviewServerConfig.setText(serverConfig);
                mPreviewServerConfig.refreshDrawableState();
            } else {
                Toast.makeText(ServerConfigActivity.this, "Phải nhập đầy đủ Cổng và Địa chỉ Server", Toast.LENGTH_LONG)
                        .show();
            }
        }
    };

    View.OnClickListener restoreDefault = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String defaultPort = OkHttpUtil.DEFAULT_SERVER_PORT;
            String defaultServerAdd = OkHttpUtil.DEFAILT_SERVER_ADDRESS;
            String serverConfig = "http://" + defaultServerAdd + ":" + defaultPort + "/api/{controller}/{param}";

            mPort.setText(defaultPort);
            mServerAddr.setText(defaultServerAdd);
            mPreviewServerConfig.setText(serverConfig);

            mPort.refreshDrawableState();
            mServerAddr.refreshDrawableState();
            mPreviewServerConfig.refreshDrawableState();
        }
    };
}
