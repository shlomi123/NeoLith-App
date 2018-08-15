package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class LOGIN extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText key;
    EditText email;
    EditText phone;
    Button button;
    String TAG = "blaaaa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Boolean key_state = sharedPreferences.getBoolean("key-flag", false);

        key = (EditText) findViewById(R.id.editTextKey);
        email = (EditText) findViewById(R.id.editTextEmail);
        phone = (EditText) findViewById(R.id.editTextPhoneNumber);
        button = (Button) findViewById(R.id.buttonNext);

        //check if key has already been entered
        if (key_state)
        {
            key.setVisibility(View.GONE);
        }

        //on button clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isNetworkAvailable(getApplicationContext()))
                {
                    //first check if an admin is trying to sign in
                    adminSignIn();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void adminSignIn()
    {
        DocumentReference doc = db.collection("Admin").document("Admin");
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Admin admin = document.toObject(Admin.class);

                        if (email.getText().toString().equals(admin.Username))
                        {
                            if (key.getText().toString().equals(admin.Password))
                            {
                                Toast.makeText(getApplicationContext(), "Welcome Admin", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LOGIN.this, ADMIN_MAIN_PAGE.class));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Admin - Wrong Password", Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
