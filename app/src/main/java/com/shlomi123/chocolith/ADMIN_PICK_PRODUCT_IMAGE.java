package com.shlomi123.chocolith;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ADMIN_PICK_PRODUCT_IMAGE extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private ImageView imageView;
    private TextView textView;
    private Button upload;
    private ProgressBar progressBar;
    private String name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private Boolean finished_upload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__pick__product__image);

        //initialize variables
        upload = (Button) findViewById(R.id.buttonUploadProduct);
        progressBar = (ProgressBar) findViewById(R.id.progressBarUpload);
        name = getIntent().getStringExtra("NAME");
        mStorageRef = FirebaseStorage.getInstance().getReference("Products");

        //make progress bar invisible until upload is clicked
        progressBar.setVisibility(View.INVISIBLE);

        //choose image for the product
        openFileChooser();

        //handle upload button click
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                uploadFile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finished_upload = false;
        imageView = (ImageView) findViewById(R.id.imageViewPickedImage);
        textView = (TextView) findViewById(R.id.textViewProductName);

        textView.setText(name);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uri = data.getData();

            Picasso.with(this).load(uri).into(imageView);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Image Was Picked", Toast.LENGTH_LONG).show();
        }
    }

    private void openFileChooser() {
        finished_upload = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        //check if image was picked
        if (uri != null)
        {
            //get reference to product folder
            StorageReference fileReference = mStorageRef.child(name + "." + getFileExtension(uri));

            //upload file
            fileReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //delay return to main page for extra affect
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finished_upload = true;
                                    startActivity(new Intent(ADMIN_PICK_PRODUCT_IMAGE.this, ADMIN_MAIN_PAGE.class));
                                    finish();
                                }
                            }, 500);

                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            //get path of uploaded product
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Product product = new Product(name, uri.toString());
                                    //upload to firestore name of product and its path for future use
                                    db.collection("Products")
                                            .add(product)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
        }
        else
        {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!finished_upload)
        {
            Toast.makeText(getApplicationContext(), "Upload Stopped!!!", Toast.LENGTH_LONG).show();
        }

    }
}
