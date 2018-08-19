package com.shlomi123.chocolith;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CLIENT_ORDER_PRODUCT extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button button;
    private ImageView imageView;
    private TextView textView;
    private EditText editText;
    private String name;
    private CircularProgressDrawable circularProgressDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__order__product);

        //product name
        name = getIntent().getStringExtra("NAME");

        //initialize views
        button = (Button) findViewById(R.id.buttonOrderSpecificProduct);
        imageView = (ImageView) findViewById(R.id.imageViewImageOfProduct);
        textView = (TextView) findViewById(R.id.textViewProductName1);
        editText = (EditText) findViewById(R.id.editTextQuantity);
        circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //set text view to product name
        textView.setText(name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO order product
            }
        });

        setProductPicture(this);
    }

    //getting picture of product
    private void setProductPicture(final Context context)
    {
        CollectionReference products = db.collection("Products");

        //get the url of the picture
        products.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //iterate through product urls
                    for (DocumentSnapshot document: task.getResult())
                    {
                        Product currProduct = document.toObject(Product.class);
                        if (currProduct.getName().equals(name))
                        {
                            //load pic into image view with url
                            StorageReference storageReference = storage.getReferenceFromUrl(currProduct.getImageUrl());
                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .fitCenter()
                                    .placeholder(circularProgressDrawable)
                                    .into(imageView);
                        }
                    }
                }
                else
                {
                    //product not found
                    Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_LONG).show();
                    Picasso.with(getApplicationContext()).load(R.mipmap.ic_launcher).into(imageView);
                }
            }
        });
    }
}
