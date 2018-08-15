package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ADMIN_STORE_ID extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__store__id);

        TextView textView = (TextView) findViewById(R.id.textViewStoreID);
        Button button = (Button) findViewById(R.id.buttonReturnToMain);

        textView.setText(getIntent().getStringExtra("STORE_ID"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_STORE_ID.this, ADMIN_MAIN_PAGE.class));
            }
        });
    }
}
