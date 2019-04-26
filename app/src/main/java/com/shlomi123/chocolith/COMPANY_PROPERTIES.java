package com.shlomi123.chocolith;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class COMPANY_PROPERTIES extends AppCompatActivity {

    private Button button;
    private Button chooseFile;
    private ImageView mImageView;
    private Uri mImageUri;
    private Boolean imageFlag = false;
    private TextView textView;
    private ProgressBar progressBar;
    private EditText name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;
    private SharedPreferences sharedPreferences;
    private String email;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__properties);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        button = (Button) findViewById(R.id.buttonCompanyName);
        textView = (TextView) findViewById(R.id.textViewCompanyName);
        name = (EditText) findViewById(R.id.editTextCompanyName);
        chooseFile = (Button) findViewById(R.id.button_open_file_chooser_for_profile);
        mImageView = (ImageView) findViewById(R.id.imageView_company_profile);
        email = mAuth.getCurrentUser().getEmail();
        mStorageRef = FirebaseStorage.getInstance().getReference("Profiles");
        progressBar = (ProgressBar) findViewById(R.id.progressBar_company_profile_upload);
        //make progress bar invisible until upload is clicked
        progressBar.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "enter company name", Toast.LENGTH_SHORT).show();
                } else if (!imageFlag){
                    Toast.makeText(getApplicationContext(), "pick a profile picture", Toast.LENGTH_SHORT).show();
                } else {
                    uploadCompany();
                }
            }
        });

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
            imageFlag = true;
        }
    }

    private void uploadCompany(){
        final StorageReference fileReference = mStorageRef.child(name.getText().toString() + "." + getFileExtension(mImageUri));


        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            // check that company name doesn't already exist
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //if company name exists return
                        if (document != null)
                        {
                            if (document.getString("Name").toLowerCase() == name.getText().toString().toLowerCase()) {
                                Toast.makeText(getApplicationContext(), "that company name already exists.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    chooseFile.setVisibility(View.INVISIBLE);
                    mImageView.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    //upload profile picture
                    fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //get path of uploaded product
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //upload store properties to database
                                            final String path = uri.toString();
                                            final Map<String, Object> map = new HashMap<>();
                                            map.put("Name", name.getText().toString());
                                            map.put("Email", email);
                                            map.put("Profile", path);



                                            //add company to database
                                            db.collection("Companies").document(email).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        //save company id
                                                        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    for (DocumentSnapshot currentDocumentSnapshot : task.getResult())
                                                                    {
                                                                        String current_name = currentDocumentSnapshot.getString("Name");
                                                                        if (current_name.equals(name.getText().toString()))
                                                                        {
                                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                                    .setDisplayName(name.getText().toString())
                                                                                    .build();

                                                                            user.updateProfile(profileUpdates)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                editor.putString("COMPANY_PROFILE", path);
                                                                                                editor.apply();
                                                                                                FirebaseAuth.getInstance().signOut();
                                                                                                startActivity(new Intent(COMPANY_PROPERTIES.this, COMPANY_SIGN_IN.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                                                finish();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
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
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                //update progress bar during upload
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressBar.setProgress((int) progress);
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
