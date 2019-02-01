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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class STORE_SIGN_IN extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText email;
    private EditText password;
    private Button button;
    private ProgressBar spinner;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__sign__in);

        //change entry point to company sign in
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USER_TYPE", 4);
        editor.apply();

        email = (EditText) findViewById(R.id.editText_store_signIn_email);
        password = (EditText) findViewById(R.id.editText_store_signIn_password);
        button = (Button) findViewById(R.id.button_store_signIn);
        spinner = (ProgressBar)findViewById(R.id.progressBar_store_signIn);
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
                                    editor.putString("STORE_EMAIL", email.getText().toString());
                                    editor.putInt("USER_TYPE", 2);
                                    editor.apply();
                                    startActivity(new Intent(STORE_SIGN_IN.this, STORE_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
    }
}
