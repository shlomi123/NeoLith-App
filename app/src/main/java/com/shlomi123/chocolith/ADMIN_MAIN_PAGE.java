package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ADMIN_MAIN_PAGE extends AppCompatActivity {

    Button delete;
    Button add;
    Button viewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        //ask permission to send sms
        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS},1);

        delete = (Button) findViewById(R.id.buttonDelete);
        add = (Button) findViewById(R.id.buttonAdd);
        viewOrders = (Button) findViewById(R.id.buttonViewOrders);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_DELETE_STORE.class));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
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
    }
}
