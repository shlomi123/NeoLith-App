package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ADMIN_FEEDBACK extends AppCompatActivity {
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__feedback);

        mEditTextSubject = findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    private void sendMail() {
        String recipientList = "info@techfficient.software";

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        SendMail sendMail = new SendMail(ADMIN_FEEDBACK.this, recipientList, subject, message, 1);
        sendMail.execute();
    }
}
