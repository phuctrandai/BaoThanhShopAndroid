package com.practice.phuc.baothanhshop;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practice.phuc.baothanhshop.Utils.OkHttpUtil;

import java.util.Objects;

public class ServerConfigActivity extends AppCompatActivity {

    private EditText mPort;
    private EditText mServerAddr;
    private Button mConfigBtn;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);
        Toolbar toolbar = findViewById(R.id.toolbar_server_config);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPort = findViewById(R.id.et_port);
        mServerAddr = findViewById(R.id.et_server_address);
        mConfigBtn = findViewById(R.id.btn_config);
        mConfigBtn.setOnClickListener(configBtnClickListener);

        mSharedPreferences = getSharedPreferences(OkHttpUtil.SHARED_PRE_SERVER, MODE_PRIVATE);
        String oldPort = OkHttpUtil.getServerPort(this);
        String oldServerAddr = OkHttpUtil.getServerAddress(this);
        mPort.append(oldPort);
        mServerAddr.append(oldServerAddr);
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
            } else {
                Toast.makeText(ServerConfigActivity.this, "Phải nhập đầy đủ Cổng và Địa chỉ Server", Toast.LENGTH_LONG)
                        .show();
            }
        }
    };
}
