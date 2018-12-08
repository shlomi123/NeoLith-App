package com.shlomi123.chocolith;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

//TODO check if product already exists
//TODO check what happens when there's no internet
public class ADMIN_ADD_PRODUCT extends AppCompatActivity {

    private Button chooseFile;
    private Button upload;
    private EditText name;
    private EditText cost;
    private EditText units;
    private Uri mImageUri;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private Boolean imageFlag = false;
    private StorageReference mStorageRef;
    private String company_name;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add__product);

        chooseFile = (Button) findViewById(R.id.button_open_file_chooser);
        mImageView = (ImageView) findViewById(R.id.imageView_product_image);
        upload = (Button) findViewById(R.id.button_upload_product);
        name = (EditText)findViewById(R.id.editText_product_name);
        cost = (EditText)findViewById(R.id.editText_cost_per_unit);
        units = (EditText)findViewById(R.id.editText_units_per_package);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_product_upload);
        //make progress bar invisible until upload is clicked
        progressBar.setVisibility(View.INVISIBLE);

        mStorageRef = FirebaseStorage.getInstance().getReference("Products");

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        company_name = sharedPreferences.getString("COMPANY_NAME", null);


        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().length() > 0
                        && cost.getText().toString().trim().length() > 0
                        && units.getText().toString().trim().length() > 0
                        && imageFlag)
                {
                    chooseFile.setVisibility(View.INVISIBLE);
                    mImageView.setVisibility(View.INVISIBLE);
                    upload.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    cost.setVisibility(View.INVISIBLE);
                    units.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    uploadProduct();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "did you enter everything?", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void uploadProduct(){
        //get reference to product image folder
        StorageReference fileReference = mStorageRef.child(name.getText().toString() + "." + getFileExtension(mImageUri));

        //upload file
        fileReference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //get path of uploaded product
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final Product product = new Product(name.getText().toString(), uri.toString(), Integer.parseInt(cost.getText().toString()), Integer.parseInt(units.getText().toString()));
                                //upload to firestore name, image path, cost per unit, and units per package

                                db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful())
                                        {
                                            DocumentSnapshot documentSnapshot = null;
                                            for (DocumentSnapshot currentDocumentSnapshot : task.getResult())
                                            {
                                                String name = currentDocumentSnapshot.getString("Name");
                                                if (name.equals(company_name))
                                                {
                                                    documentSnapshot = currentDocumentSnapshot;
                                                }
                                            }

                                            String id = documentSnapshot.getId();

                                            db.collection("Companies").document(id).collection("Products")
                                                    .add(product)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                                                finish();
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });
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
    }
}
