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

    Button delete;
    Button add;
    Button viewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        //ask permission to send sms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!checkPermission())
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //if permission is denied ask again, only until allowed continue
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission())
            {
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
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, 1);
    }
}
