package com.shlomi123.chocolith;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CLIENT_MAIN_PAGE extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Button scanner;
    private Button products;
    private Button history;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__main__page);

        scanner = (Button) findViewById(R.id.buttonQRScanner);
        products = (Button) findViewById(R.id.buttonShowAllProducts);
        history = (Button) findViewById(R.id.buttonViewHistory);
        progressBar = (ProgressBar) findViewById(R.id.progress_circle_loading);
        scanner.setVisibility(View.INVISIBLE);
        products.setVisibility(View.INVISIBLE);
        history.setVisibility(View.INVISIBLE);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent signInIntent = new Intent(CLIENT_MAIN_PAGE.this, LOGIN.class);
                    signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(signInIntent);
                }
                else
                {
                    final String adminUID = firebaseAuth.getUid();
                    db = FirebaseFirestore.getInstance();
                    DocumentReference doc = db.collection("Admin").document("Admin");
                    doc.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        Admin admin1 = documentSnapshot.toObject(Admin.class);
                                        if (admin1.getAuthUID().equals(adminUID))
                                        {
                                            Intent adminActivity = new Intent(CLIENT_MAIN_PAGE.this, ADMIN_MAIN_PAGE.class);
                                            adminActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(adminActivity);
                                            finish();
                                        }
                                        else
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            scanner.setVisibility(View.VISIBLE);
                                            products.setVisibility(View.VISIBLE);
                                            history.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        };
    }

    @Override
    protected void onStart(){
        super.onStart();


        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_QR_SCANNER.class));
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CLIENT_MAIN_PAGE.this, CLIENT_SHOW_ALL_PRODUCTS.class));
            }
        });
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                firebaseAuth.signOut();
                Intent loginIntent = new Intent(CLIENT_MAIN_PAGE.this, LOGIN.class);
                startActivity(loginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
