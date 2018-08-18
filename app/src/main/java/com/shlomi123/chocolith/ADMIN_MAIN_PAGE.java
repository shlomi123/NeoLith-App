package com.shlomi123.chocolith;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import static android.Manifest.permission.SEND_SMS;


public class ADMIN_MAIN_PAGE extends AppCompatActivity {

    private Button delete;
    private Button addStore;
    private Button viewOrders;
    private Button addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        delete = (Button) findViewById(R.id.buttonDelete);
        addStore = (Button) findViewById(R.id.buttonAddStore);
        viewOrders = (Button) findViewById(R.id.buttonViewOrders);
        addProduct = (Button) findViewById(R.id.buttonAddProduct);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_DELETE_STORE.class));
            }
        });

        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_ADD_STORE.class));
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_SCAN_PRODUCT.class));
            }
        });
    }
}
