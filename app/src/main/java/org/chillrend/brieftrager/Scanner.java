package org.chillrend.brieftrager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ZXingScannerView ScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
    }

    @Override
    public void handleResult(Result result) {
        PackageTracking.tvResult.setText(result.getText());
        Intent i = new Intent(getApplicationContext(), PackageTracking.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ScannerView.stopCamera();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}
