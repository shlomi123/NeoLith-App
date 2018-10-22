package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ENTRY_POINT extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry__point);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        int user_type = sharedPreferences.getInt("USER_TYPE", 0);

        switch (user_type)
        {
            case 0:
                startActivity(new Intent(ENTRY_POINT.this, CHOOSE_USER_TYPE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case 1:
                startActivity(new Intent(ENTRY_POINT.this, ADMIN_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case 2:
                startActivity(new Intent(ENTRY_POINT.this, CLIENT_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
    }
}
