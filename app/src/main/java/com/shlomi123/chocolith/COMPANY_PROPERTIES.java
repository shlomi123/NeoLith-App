package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class COMPANY_PROPERTIES extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private EditText name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__properties);

        button = (Button) findViewById(R.id.buttonCompanyName);
        textView = (TextView) findViewById(R.id.textViewCompanyName);
        name = (EditText) findViewById(R.id.editTextCompanyName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("Name", name.getText().toString());
                db.collection("Companies").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(COMPANY_PROPERTIES.this, ADMIN_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
