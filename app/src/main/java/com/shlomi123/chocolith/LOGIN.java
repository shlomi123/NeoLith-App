package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LOGIN extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    EditText key;
    EditText email;
    EditText phone;
    Button SignIn;
    Button SignOut;
    String TAG = "blaaaa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final Boolean has_signed_in = sharedPreferences.getBoolean("key-flag", false);

        key = (EditText) findViewById(R.id.editTextKey);
        email = (EditText) findViewById(R.id.editTextEmail);
        phone = (EditText) findViewById(R.id.editTextPhoneNumber);
        SignIn = (Button) findViewById(R.id.buttonSignIn);
        SignOut = (Button) findViewById(R.id.buttonSignOut);


        //check if key has already been entered
        if (has_signed_in)
        {
            key.setVisibility(View.GONE);
        }

        //on button clicked
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for internet
                if (Helper.isNetworkAvailable(getApplicationContext()))
                {
                    //first check if an admin is trying to sign in

                    if (phone.getText().toString().equals(""))
                    {
                        adminSignIn();
                    }
                    else
                    {
                        //check if its the first sign in
                        if (has_signed_in)
                        {
                            signIn();
                        }
                        else
                        {
                            firstSignIn();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("key-flag", false);
                editor.apply();
                finish();
            }
        });
    }

    //on first sign in
    private void firstSignIn()
    {
        CollectionReference stores = db.collection("Stores");
        Query store = stores.whereEqualTo("_email", email.getText().toString()).whereEqualTo("_phone", Integer.parseInt(phone.getText().toString()));

        store.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                // iterate through snapshots (there is only one snapshot because store names are unique)
                for (DocumentSnapshot documentSnapshot : documents)
                {
                    if(documentSnapshot.getId().equals(key.getText().toString()))
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("key-flag", true);
                        editor.putString("ID", documentSnapshot.getId());
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "login successful blaaa", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LOGIN.this, CLIENT_MAIN_PAGE.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "key is incorrect", Toast.LENGTH_LONG).show();
                    }
                }
                // if no store was found according to query
                if (documents.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "email or phone number is incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //after first sign in
    private void signIn()
    {
        CollectionReference stores = db.collection("Stores");
        Query store = stores.whereEqualTo("_email", email.getText().toString()).whereEqualTo("_phone", Integer.parseInt(phone.getText().toString()));

        store.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                // iterate through snapshots (there is only one snapshot because store names are unique)
                for (DocumentSnapshot documentSnapshot : documents)
                {
                    Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LOGIN.this, CLIENT_MAIN_PAGE.class));
                }
                // if no store was found according to query
                if (documents.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "email or phone number is incorrect", Toast.LENGTH_LONG).show();
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
