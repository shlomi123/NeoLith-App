package com.shlomi123.chocolith;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class CLIENT_MAIN_PAGE extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__main__page);

        //request permission to access camera
        Button scanner = (Button) findViewById(R.id.buttonQRScanner);

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_QR_SCANNER.class));
            }
        });
    }
}
