package com.shlomi123.chocolith;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class DistributorProfileFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.distributor_profile_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        profile_path = sharedPreferences.getString("COMPANY_PROFILE", null);
        email = sharedPreferences.getString("COMPANY_EMAIL", null);
        name = sharedPreferences.getString("COMPANY_NAME", null);
        change_profile = getActivity().findViewById(R.id.buttonPickNewPicture);
        profile_picture = getActivity().findViewById(R.id.imageViewCurrentProfilePicture);
        progressBar = getActivity().findViewById(R.id.progressBar_company_new_profile_upload);
        //make progress bar invisible until upload is clicked
        progressBar.setVisibility(View.INVISIBLE);

        //add profile picture
        circularProgressDrawable = new CircularProgressDrawable(getActivity());
        circularProgressDrawable.start();
        StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
        GlideApp.with(getContext())
                .load(storageReference)
                .placeholder(circularProgressDrawable)
                .fitCenter()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(profile_picture);

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
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
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
                                                        }
                                                    }
                                                });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            /*uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                //Toast.makeText(getContext(), "profile picture changed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }
}
