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

        Button scanner = (Button) findViewById(R.id.buttonQRScanner);
        Button products = (Button) findViewById(R.id.buttonShowAllProducts);
        Button history = (Button) findViewById(R.id.buttonViewHistory);

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_QR_SCANNER.class));
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_SHOW_ALL_PRODUCTS.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_ORDER_HISTORY.class));
            }
        });
    }
}
