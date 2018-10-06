package com.shlomi123.chocolith;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private EditText key;
    private EditText email;
    private EditText phone;
    private TextView title;
    private Button SignIn;
    private Button SignOut;
    private String TAG = "blaaaa";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

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
        title = (TextView) findViewById(R.id.textViewTitle);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        //check if key has already been entered
        /*if (has_signed_in)
        {
            key.setVisibility(View.GONE);
        }*/

        //on button clicked
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for internet
                if (Helper.isNetworkAvailable(getApplicationContext()))
                {
                    //show only progress bar
                    progressDialog.setMessage("Signing in...");
                    progressDialog.show();
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
                            authenticate(email.getText().toString(), phone.getText().toString());
                        }
                        else
                        {
                            addAuthentication(email.getText().toString(), phone.getText().toString());
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
                mAuth.signOut();
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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("key-flag", true);
                    editor.putString("ID", documentSnapshot.getId());
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LOGIN.this, CLIENT_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
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
                    startActivity(new Intent(LOGIN.this, CLIENT_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                // if no store was found according to query
                if (documents.isEmpty())
                {
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(), "email or phone number is incorrect", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAuth.signOut();
            }
        });
    }

    private void adminSignIn()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), key.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            DocumentReference doc = db.collection("Admin").document("Admin");
                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Admin admin = document.toObject(Admin.class);
                                            Log.d("blaaaa", admin.getPassword() + " " + admin.getUsername());
                                            if (email.getText().toString().equals(admin.getUsername()))
                                            {
                                                if (key.getText().toString().equals(admin.getPassword()))
                                                {
                                                    Toast.makeText(getApplicationContext(), "Welcome Admin", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(LOGIN.this, ADMIN_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    finish();
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
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void addAuthentication(final String email, final String phone)
    {
        mAuth.createUserWithEmailAndPassword(email, phone).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    firstSignIn();
                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        mAuth.signInWithEmailAndPassword(email, phone).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    firstSignIn();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Error regular", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void authenticate(final String email, final String phone)
    {
        mAuth.signInWithEmailAndPassword(email, phone).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    signIn();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error regular", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
