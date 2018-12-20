package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class STORE_CHOOSE_COMPANY extends AppCompatActivity {

    private AutoCompleteTextView chooseDistributor;
    private Button button;
    private Boolean list_flag = false;
    private List<String> distributors = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__choose__company);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        email = sharedPreferences.getString("STORE_EMAIL", null);
        chooseDistributor = (AutoCompleteTextView) findViewById(R.id.editTextStoreChooseCompany);
        button = (Button) findViewById(R.id.button_continue_to_main);

        createListOfDistributors();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if list is empty
                if (list_flag) {
                    String id = map.get(chooseDistributor.getText().toString());

                    // check if store is registered to company
                    db.collection("Companies")
                            .document(id)
                            .collection("Stores")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Boolean found = false;
                                        // if result is empty store is not registered
                                        if (!task.getResult().isEmpty()) {
                                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                Store store = documentSnapshot.toObject(Store.class);
                                                if (store.get_email().equals(email)) {
                                                    found = true;
                                                    editor.putString("STORE_NAME", store.get_name());
                                                    editor.apply();
                                                }
                                            }
                                            if (found) {
                                                final String id = map.get(chooseDistributor.getText().toString());

                                                db.collection("Companies")
                                                        .document(id)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    editor.putString("COMPANY_NAME", task.getResult().getString("Name"));
                                                                    editor.putString("COMPANY_EMAIL", task.getResult().getString("Email"));
                                                                    editor.putString("COMPANY_ID", id);
                                                                    editor.apply();

                                                                    startActivity(new Intent(STORE_CHOOSE_COMPANY.this, STORE_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                //main page
                                            } else {
                                                //store isn't registered
                                                Toast.makeText(getApplicationContext(), "not registered to this distributor", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            //store isn't registered
                                            Toast.makeText(getApplicationContext(), "not registered to this distributor", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "no distributors", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createListOfDistributors() {
        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String name = documentSnapshot.getString("Name");
                            distributors.add(name);
                            map.put(name, documentSnapshot.getId());
                        }
                        Toast.makeText(getApplicationContext(), "initializing list", Toast.LENGTH_SHORT).show();
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_singlechoice, distributors);
                        //Set the number of characters the user must type before the drop down list is shown
                        chooseDistributor.setThreshold(1);
                        chooseDistributor.setAdapter(adapter);

                        list_flag = true;
                    } else {
                        list_flag = false;
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
