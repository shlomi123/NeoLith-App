package com.shlomi123.chocolith;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ADMIN_ADD_STORE extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText address;
    private EditText phoneNum;
    private EditText store;
    private EditText email;
    private EditText verifyEmail;
    private Button button;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private String id;
    private ProgressBar mProgressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add__store);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("COMPANY_ID", null);

        address = (EditText) findViewById(R.id.editTextAddress);
        store = (EditText) findViewById(R.id.editTextStoreName);
        email = (EditText) findViewById(R.id.editTextEmail1);
        verifyEmail = (EditText) findViewById(R.id.editTextVerifyEmail1);
        phoneNum = (EditText) findViewById(R.id.editTextPhoneNumber1);
        button = (Button) findViewById(R.id.buttonNext1);
        mProgressCircle = findViewById(R.id.progress_circle_add_store);
        mProgressCircle.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for internet connection
                /*if (Helper.isNetworkAvailable(getApplicationContext()))
                {*/
                // verify email
                if (!email.getText().toString().equals(verifyEmail.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Email is incorrect", Toast.LENGTH_LONG).show();
                } else {
                    //show progress circle and make other views invisible
                    mProgressCircle.setVisibility(View.VISIBLE);
                    address.setVisibility(View.INVISIBLE);
                    store.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    verifyEmail.setVisibility(View.INVISIBLE);
                    phoneNum.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    //get company's document

                    db.collection("Companies")
                            .document(id)
                            .collection("Stores")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                // check that store name doesn't already exist
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            //if store name exists return
                                            if (document != null) {
                                                if (document.getString("_name") == store.getText().toString()) {
                                                    Toast.makeText(getApplicationContext(), "That store name already exists.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        }
                                        String name = store.getText().toString();
                                        String e = email.getText().toString();
                                        String a = address.getText().toString();
                                        int phone = Integer.parseInt(phoneNum.getText().toString());
                                        // if store name doesn't exist create new store
                                        addStoreToDataBase(id, name, e, a, phone);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void addStoreToDataBase(final String id, final String name, final String email, final String address, final int phone) {
        //add new store to databased
        Store s = new Store(name, email, address, phone);
        db.collection("Companies").document(id).collection("Stores")
                .add(s)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("blaaaa", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Succesfuly added", Toast.LENGTH_LONG).show();

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("blaaaa", "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Error, wasn't added", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
