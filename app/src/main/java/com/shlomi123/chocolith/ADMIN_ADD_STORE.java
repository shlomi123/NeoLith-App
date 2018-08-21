package com.shlomi123.chocolith;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.SEND_SMS;

public class ADMIN_ADD_STORE extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText address;
    EditText phoneNum;
    EditText store;
    EditText email;
    EditText verifyEmail;
    Button button;
    public static final int SENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add__store);

        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission())
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                address = (EditText) findViewById(R.id.editTextAddress);
                store = (EditText) findViewById(R.id.editTextStoreName);
                email = (EditText) findViewById(R.id.editTextEmail1);
                verifyEmail = (EditText) findViewById(R.id.editTextVerifyEmail1);
                phoneNum = (EditText) findViewById(R.id.editTextPhoneNumber1);
                button = (Button) findViewById(R.id.buttonNext1);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // check for internet connection
                        if (Helper.isNetworkAvailable(getApplicationContext()))
                        {
                            // verify email
                            if (!email.getText().toString().equals(verifyEmail.getText().toString()))
                            {
                                Toast.makeText(getApplicationContext(), "Email is incorrect", Toast.LENGTH_LONG).show();
                            }
                            else {
                                //check if store name already exists
                                CollectionReference citiesRef = db.collection("Stores");
                                citiesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    // check that store name doesn't already exist
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            boolean flag = false;
                                            for (DocumentSnapshot document : task.getResult()) {
                                                Map<String, Object> map = document.getData();
                                                //if store name exists return
                                                if (map.get("_name").toString().equals(store.getText().toString())) {
                                                    Toast.makeText(getApplicationContext(), "That username already exists.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                            String name = store.getText().toString();
                                            String e = email.getText().toString();
                                            String a = address.getText().toString();
                                            int phone = Integer.parseInt(phoneNum.getText().toString());
                                            // if store name doesn't exist create new store
                                            addStoreToDataBase(name, e, a, phone);
                                        } else {
                                            Log.d("blaaaa", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                requestPermission();
            }
        }
    }

    private void addStoreToDataBase(String name, String email, String address, final int phone)
    {
        //add new store to databased
        Store s = new Store(name, email, address, phone);
        db.collection("Stores")
                .add(s)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("blaaaa", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Succesfuly added", Toast.LENGTH_LONG).show();

                        sendSms(phoneNum.getText().toString(), documentReference.getId());

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

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, 1);
    }

    private void sendSms(String number, String id)
    {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, id, null, null);
            Toast.makeText(getApplicationContext(), "make sure sms was sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();

            db.collection("Stores")
                    .document(id)
                    .delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "sms wasn't sent but account was created, send key manually", Toast.LENGTH_LONG).show();
                        }
                    });

            Log.d("blaaaa", e.toString());
            e.printStackTrace();
        }
    }

}
