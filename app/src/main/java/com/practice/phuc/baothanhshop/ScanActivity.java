package com.practice.phuc.baothanhshop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {
        if (result == null) {
            setResult(RESULT_CANCELED);
        } else if (result.getText() == null) {
            setResult(Activity.RESULT_CANCELED);
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.EXTRA_KEY_BARCODE, result.getText());
            setResult(Activity.RESULT_OK, resultIntent);
        }
        onBackPressed();
    }

    @Override
    protected void onResume() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.stopCamera();
        super.onPause();
    }
}
