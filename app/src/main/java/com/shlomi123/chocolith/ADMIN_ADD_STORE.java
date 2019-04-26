package com.shlomi123.chocolith;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ADMIN_ADD_STORE extends AppCompatActivity implements StoreSearchAdapter.OnItemClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText search;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private String company_name;
    private String company_email;
    private String company_profile;
    private ProgressBar mProgressCircle;
    private RecyclerView mRecyclerView;
    private StoreSearchAdapter mAdapter;
    private List<Store> mStores;
    final private List<String> mExistingStores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add__store);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        company_name = mAuth.getCurrentUser().getDisplayName();
        company_email = mAuth.getCurrentUser().getEmail();
        company_profile = sharedPreferences.getString("COMPANY_PROFILE", null);

        search = (EditText) findViewById(R.id.editText_search);
        mProgressCircle = findViewById(R.id.progress_circle_add_store);

        mRecyclerView = findViewById(R.id.recycler_view_add_store);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mStores = new ArrayList<>();
        mAdapter = new StoreSearchAdapter(getApplicationContext(), mStores);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ADMIN_ADD_STORE.this);

        db.collection("Companies")
                .document(company_email)
                .collection("Stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                mExistingStores.add(documentSnapshot.toObject(Store.class).get_email());
                            }

                            db.collection("Stores")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                return;
                                            }
                                            // create recycler view to show stores and their data
                                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                                mStores.clear();
                                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                    Store store = document.toObject(Store.class);
                                                    if (!mExistingStores.contains(store.get_email())){
                                                        mStores.add(store);
                                                    }
                                                }

                                                mAdapter.notifyDataSetChanged();

                                                mProgressCircle.setVisibility(View.INVISIBLE);
                                            } else {
                                                mProgressCircle.setVisibility(View.INVISIBLE);
                                                mStores.clear();
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for internet connection
                /*if (Helper.isNetworkAvailable(getApplicationContext()))
                {
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
                            .document(company_email)
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
                                        addStoreToDataBase(name, e, a, phone);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });*/
    }

    private void addStoreToDataBase(final String name, final String email) {
        //add new store to databased
        final Store s = new Store(name, email);
        db.collection("Companies")
                .document(company_email)
                .collection("Stores")
                .document(email)
                .set(s)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final Distributor distributor = new Distributor(company_name, company_email, company_profile);

                        db.collection("Stores")
                                .document(email)
                                .collection("Distributors")
                                .document(company_email)
                                .set(distributor)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Succesfuly added", Toast.LENGTH_LONG).show();
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Error, wasn't added", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });
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

    @Override
    public void onItemClick(Store store) {
        addStoreToDataBase(store.get_name(), store.get_email());
    }
}
