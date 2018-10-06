package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class COMPANY_PROPERTIES extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__properties);

        button = (Button) findViewById(R.id.buttonCompanyName);
        textView = (TextView) findViewById(R.id.textViewCompanyName);
        name = (EditText) findViewById(R.id.editTextCompanyName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(COMPANY_PROPERTIES.this, COMPANY_REGISTER.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("NAME", name.getText().toString());
                startActivity(intent);
            }
        });
    }
}
