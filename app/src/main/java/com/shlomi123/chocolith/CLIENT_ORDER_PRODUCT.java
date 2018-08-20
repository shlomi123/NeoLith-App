package com.shlomi123.chocolith;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.common.reflect.TypeToken;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONStringer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.internal.JsonParser;

public class CLIENT_ORDER_PRODUCT extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button button;
    private ImageView imageView;
    private TextView textView;
    private EditText editText;
    private String name;
    private CircularProgressDrawable circularProgressDrawable;
    private boolean mailClientOpened = false;
    private int quantity;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__order__product);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);
        //product name
        name = getIntent().getStringExtra("NAME");

        //initialize views
        button = (Button) findViewById(R.id.buttonOrderSpecificProduct);
        imageView = (ImageView) findViewById(R.id.imageViewImageOfProduct);
        textView = (TextView) findViewById(R.id.textViewProductName1);
        editText = (EditText) findViewById(R.id.editTextQuantity);
        circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //set text view to product name
        textView.setText(name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Pick a Quantity to Order", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    quantity = Integer.parseInt(editText.getText().toString());
                    //first verify that the users wants to place the order
                    showAlertDialog(CLIENT_ORDER_PRODUCT.this);
                }
            }
        });

        setProductPicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if user didn't open email client flag is false and order won't be logged
        mailClientOpened = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if user opened email client flag is true
        mailClientOpened = true;
    }

    //getting picture of product
    private void setProductPicture()
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

    //alert dialog
    private void showAlertDialog(final Context context)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Place Order");
        // make text according to entered quantity
        if (quantity == 1) {
            alertDialog.setMessage("You want to order " + editText.getText().toString() + " box of product");
        }
        else if(quantity == 0) {
            alertDialog.setMessage("Please place an order of more that one product");
        }
        else {
            alertDialog.setMessage("You want to order " + editText.getText().toString() + " boxes of product");
        }

        //if the user verifies the order
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (quantity == 0)
                {
                    dialogInterface.dismiss();
                }
                else
                {
                    //first make sure he sends the email before order is logged
                    sendMail(context,name);
                    dialogInterface.dismiss();
                }
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check if user opened email client
        if(requestCode == 1 && mailClientOpened){
            orderProduct(getApplicationContext());
        }
        else
        {
            Toast.makeText(getApplicationContext(), "You must send email in order to complete the order", Toast.LENGTH_SHORT).show();
        }
    }

    private void orderProduct(final Context context)
    {
        if (id != null)
        {
            HashMap<String, Object> order = new HashMap<>();
            order.put("Date", Timestamp.now().toDate());
            order.put("Product", name);
            order.put("Quantity", quantity);

            db.collection("Stores")
                    .document(id)
                    .update("orders", FieldValue.arrayUnion(order))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                finish();
                            }
                            else
                            {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail(final Context context, final String name)
    {
        if (id != null)
        {
            //get store details for email
            db.collection("Stores").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Store store = documentSnapshot.toObject(Store.class);


                        //make email
                        String[] TO = {"yogroner@gmail.com"};
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);

                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Order");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Store: " + store.get_name() + "\n" +
                                "Address: " + store.get_address() + "\n" +
                                "Order: " + String.valueOf(quantity) + " boxes of " + name);

                        try {
                            //open email client
                            startActivityForResult(emailIntent, 1);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "id incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }

    }
}
