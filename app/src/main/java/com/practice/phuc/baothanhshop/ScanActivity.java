package com.practice.phuc.baothanhshop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.stopCamera();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.setResultHandler(this);
                    mScannerView.startCamera();

                } else {
                    ScanActivity.this.finish();
                }
                break;

            default:
                break;
        }
    }
}
