package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class COMPANY_REGISTER extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button verify;
    private EditText email;
    private EditText password;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean sign_in_flag = false;
    private ProgressBar spinner;
    private TextView textView;
    private EditText verify_password;
    private TextView title;
    private TextView logIn;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__register);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);


        verify = (Button) findViewById(R.id.ButtonCompanyVerify);
        email = (EditText) findViewById(R.id.editTextCompanyEmail);
        password = (EditText) findViewById(R.id.editTextCompanyPassword);
        verify_password = (EditText) findViewById(R.id.editTextCompanyPasswordVerify);
        title = (TextView) findViewById(R.id.textViewCompanyRegister);
        logIn = (TextView) findViewById(R.id.textView_log_in);
        textView = (TextView) findViewById(R.id.textViewInstructions);
        textView.setVisibility(View.GONE);
        spinner=(ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create user
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.removeAuthStateListener(mAuthListener);
                startActivity(new Intent(COMPANY_REGISTER.this, COMPANY_SIGN_IN.class));
            }
        });

        // listens for user sign in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    sign_in_flag = true;
                    // User is signed in
                    //send verification mail
                    sendVerificationEmail();
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    /*@Override
    protected void onResume()
    {
        // when user returns to app check if signed in before
        super.onResume();
        if(sign_in_flag)
        {
            // if user signed in before check if he verified his email
            mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user.isEmailVerified())
                        {
                            editor = sharedPreferences.edit();
                            // user is verified, start company properties activity
                            editor.putString("COMPANY_EMAIL", email.getText().toString());
                            editor.apply();
                            mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(new Intent(COMPANY_REGISTER.this, COMPANY_PROPERTIES.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        else
                        {
                            // if user returned to application without verifying email
                            Toast.makeText(getApplicationContext(), "email wasn't verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }*/

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        spinner.setVisibility(View.VISIBLE);
        verify.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        verify_password.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);
        logIn.setVisibility(View.INVISIBLE);

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            mAuth.removeAuthStateListener(mAuthListener);
                            Toast.makeText(getApplicationContext(), "verification email sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(COMPANY_REGISTER.this, COMPANY_PROPERTIES.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            spinner.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            verify.setVisibility(View.VISIBLE);
                            password.setVisibility(View.VISIBLE);
                            verify_password.setVisibility(View.VISIBLE);
                            email.setVisibility(View.VISIBLE);
                            title.setVisibility(View.VISIBLE);
                            logIn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
