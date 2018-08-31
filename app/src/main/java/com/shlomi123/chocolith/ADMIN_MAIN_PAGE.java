package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class ADMIN_MAIN_PAGE extends AppCompatActivity {

    private Button delete;
    private Button addStore;
    private Button viewOrders;
    private Button addProduct;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);
        firebaseAuth = FirebaseAuth.getInstance();

        delete = (Button) findViewById(R.id.buttonDelete);
        addStore = (Button) findViewById(R.id.buttonAddStore);
        viewOrders = (Button) findViewById(R.id.buttonViewOrders);
        addProduct = (Button) findViewById(R.id.buttonAddProduct);
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

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
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_VIEW_STORES.class));
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_SCAN_PRODUCT.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                firebaseAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("key-flag", false);
                editor.apply();
                Intent loginIntent = new Intent(ADMIN_MAIN_PAGE.this, LOGIN.class);
                startActivity(loginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
