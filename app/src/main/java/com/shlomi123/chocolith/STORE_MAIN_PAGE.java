package com.shlomi123.chocolith;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class STORE_MAIN_PAGE extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__main__page);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        TextView textView = (TextView) findViewById(R.id.textViewTest);

        String store_name = sharedPreferences.getString("STORE_NAME", null);
        String store_email = sharedPreferences.getString("STORE_EMAIL", null);

        String company_name = sharedPreferences.getString("COMPANY_NAME", null);
        String company_email = sharedPreferences.getString("COMPANY_EMAIL", null);
        String company_id = sharedPreferences.getString("COMPANY_ID", null);

        textView.setText("store name: " + store_name + "\n" +
                "store email: " + store_email + "\n" +
                "company name: " + company_name + "\n" +
                "company email: " + company_email + "\n" +
                "company id: " + company_id + "\n");
    }
}
