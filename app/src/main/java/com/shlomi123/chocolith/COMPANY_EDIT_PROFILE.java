package com.shlomi123.chocolith;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class COMPANY_EDIT_PROFILE extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String profile_path;
    private String email;
    private String name;
    private ImageView profile_picture;
    private Button change_profile;
    private CircularProgressDrawable circularProgressDrawable;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ProgressBar progressBar;
    private Uri mImageUri;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company__edit__profile);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        profile_path = sharedPreferences.getString("COMPANY_PROFILE", null);
        email = mAuth.getCurrentUser().getEmail();
        name = mAuth.getCurrentUser().getDisplayName();
        change_profile = findViewById(R.id.buttonPickNewPicture);
        profile_picture = findViewById(R.id.imageViewCurrentProfilePicture);
        progressBar = findViewById(R.id.progressBar_company_new_profile_upload_blaaa);
        //make progress bar invisible until upload is clicked
        progressBar.setVisibility(View.INVISIBLE);
        //add profile picture
        circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
        circularProgressDrawable.start();
        final StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                GlideApp.with(getApplicationContext())
                        .load(storageReference)
                        .fitCenter()
                        .signature(new ObjectKey(storageMetadata.getCreationTimeMillis()))
                        .placeholder(circularProgressDrawable)
                        .into(profile_picture);
            }
        });

        change_profile.setOnClickListener(new View.OnClickListener() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
            //UploadTask uploadTask = storageReference.putFile(mImageUri);

            progressBar.setVisibility(View.VISIBLE);
            profile_picture.setVisibility(View.INVISIBLE);
            change_profile.setVisibility(View.INVISIBLE);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        StorageReference newStorageReference = FirebaseStorage.getInstance()
                                .getReference("Profiles")
                                .child(name + "." + getFileExtension(mImageUri));

                        newStorageReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String path = uri.toString();

                                        db.collection("Companies")
                                                .document(email)
                                                .update("Profile", path)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("COMPANY_PROFILE", path);
                                                            editor.apply();
                                                            finish();
                                                        }
                                                    }
                                                });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });

                    }else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
