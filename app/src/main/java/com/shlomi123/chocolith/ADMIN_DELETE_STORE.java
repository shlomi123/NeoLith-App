package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ADMIN_DELETE_STORE extends AppCompatActivity {

    Button byName;
    Button byAddress;
    Button byEmail;
    Button byPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__delete__store);

        byName = (Button) findViewById(R.id.buttonDeleteByStoreName);
        byAddress = (Button) findViewById(R.id.buttonDeleteByAddress);
        byEmail = (Button) findViewById(R.id.buttonDeleteByEmail);
        byPhone = (Button) findViewById(R.id.buttonDeleteByPhoneNumber);

        byName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ADMIN_CHOOSE_STORE_TO_DELETE.class);
                intent.putExtra("QUERY_TYPE", "NAME");
                startActivity(intent);
            }
        });

        byAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ADMIN_CHOOSE_STORE_TO_DELETE.class);
                intent.putExtra("QUERY_TYPE", "ADDRESS");
                startActivity(intent);
            }
        });

        byEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ADMIN_CHOOSE_STORE_TO_DELETE.class);
                intent.putExtra("QUERY_TYPE", "EMAIL");
                startActivity(intent);
            }
        });

        byPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ADMIN_CHOOSE_STORE_TO_DELETE.class);
                intent.putExtra("QUERY_TYPE", "PHONE");
                startActivity(intent);
            }
        });
    }
}
