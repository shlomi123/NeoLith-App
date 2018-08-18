package com.shlomi123.chocolith;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class ADMIN_CHOOSE_STORE_TO_DELETE extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String query_type;
    EditText editText;
    Button button;
    final CollectionReference stores = db.collection("Stores");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__choose__store__to__delete);

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

        query_type = getIntent().getStringExtra("QUERY_TYPE");
        editText = (EditText) findViewById(R.id.editTextQueryToDelete);
        button = (Button) findViewById(R.id.buttonDeleteTheStore);

        // change edit text type according to query type
        switch (query_type){
            case "NAME":
                editText.setHint("store name");
                break;
            case "ADDRESS":
                editText.setHint("address");
                break;
            case "EMAIL":
                editText.setHint("email");
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "PHONE":
                editText.setHint("phone number");
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for internet
                if(Helper.isNetworkAvailable(getApplicationContext()))
                {
                    Query store;
                    // delete according to the query type (ex: by name or by phone number)
                    switch (query_type){
                        case "NAME":
                            store = stores.whereEqualTo("_name", editText.getText().toString());
                            deleteTheStore(store);
                            break;
                        case "ADDRESS":
                            store = stores.whereEqualTo("_address", editText.getText().toString());
                            deleteTheStore(store);
                            break;
                        case "EMAIL":
                            store = stores.whereEqualTo("_email", editText.getText().toString());
                            deleteTheStore(store);
                            break;
                        case "PHONE":
                            store = stores.whereEqualTo("_phone", Integer.parseInt(editText.getText().toString()));
                            deleteTheStore(store);
                            break;
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void deleteTheStore(Query store)
    {
        // delete the store the snapshot that was returned form query
        store.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                // iterate through snapshots (there is only one snapshot because store names are unique)
                for (DocumentSnapshot documentSnapshot : documents)
                {
                    stores.document(documentSnapshot.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                // deletion was successful
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Store Successfully Deleted", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ADMIN_CHOOSE_STORE_TO_DELETE.this, ADMIN_MAIN_PAGE.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                // deletion was unsuccessful
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to Delete Store", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ADMIN_CHOOSE_STORE_TO_DELETE.this, ADMIN_MAIN_PAGE.class));
                                }
                            });
                }
                // if no store was found according to query
                if (documents.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Store Not Found", Toast.LENGTH_LONG).show();
                }
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
}
