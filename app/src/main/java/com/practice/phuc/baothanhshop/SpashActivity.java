package com.practice.phuc.baothanhshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.practice.phuc.baothanhshop.Utils.OkHttpUtil;

public class SpashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        new SetupServerTask().execute((String) null);
    }

    @SuppressLint("StaticFieldLeak")
    private class SetupServerTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String port = OkHttpUtil.getServerPort(SpashActivity.this);
            String address = OkHttpUtil.getServerAddress(SpashActivity.this);

            port = port == null ? OkHttpUtil.DEFAULT_SERVER_PORT : port;
            address = address == null ? OkHttpUtil.DEFAILT_SERVER_ADDRESS : address;

            SharedPreferences sp = SpashActivity.this.getSharedPreferences(OkHttpUtil.SHARED_PRE_SERVER, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(OkHttpUtil.SHARED_PRE_KEY_SERVER_PORT, port);
            editor.putString(OkHttpUtil.SHARED_PRE_KEY_SERVER_ADDRESS, address);
            editor.apply();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Intent mainIntent = new Intent(SpashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            SpashActivity.this.finish();
        }
    }
}
