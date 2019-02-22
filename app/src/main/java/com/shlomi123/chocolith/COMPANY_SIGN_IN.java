package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class COMPANY_SIGN_IN extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText email;
    private EditText password;
    private Button button;
    private Button change_type;
    private ProgressBar spinner;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__sign__in);

        //change entry point to company sign in
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USER_TYPE", 3);
        editor.apply();

        email = (EditText) findViewById(R.id.editText_signIn_email);
        password = (EditText) findViewById(R.id.editText_signIn_password);
        button = (Button) findViewById(R.id.button_signIn);
        change_type = (Button) findViewById(R.id.button_company_change_user_type);
        spinner = (ProgressBar)findViewById(R.id.progressBar_signIn);
        spinner.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email.setVisibility(View.INVISIBLE);
                password.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    changeCompanyId();
                                }else{
                                    email.setVisibility(View.VISIBLE);
                                    password.setVisibility(View.VISIBLE);
                                    button.setVisibility(View.VISIBLE);
                                    spinner.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        change_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("USER_TYPE", 0);
                editor.apply();
                startActivity(new Intent(COMPANY_SIGN_IN.this, ENTRY_POINT.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
    }

    private void changeCompanyId(){
        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot currDocument: task.getResult())
                    {
                        if (currDocument != null)
                        {
                            if (currDocument.getString("Email").equals(email.getText().toString())) {
                                // change company details, in the case that he signs in as a different company
                                editor.putString("COMPANY_EMAIL", email.getText().toString());
                                editor.putString("COMPANY_NAME", currDocument.getString("Name"));
                                editor.putString("COMPANY_PROFILE", currDocument.getString("Profile"));
                                editor.putInt("USER_TYPE", 1);
                                editor.apply();
                                startActivity(new Intent(COMPANY_SIGN_IN.this, ADMIN_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
