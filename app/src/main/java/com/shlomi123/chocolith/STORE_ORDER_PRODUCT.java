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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

//TODO work on ui
public class STORE_ORDER_PRODUCT extends AppCompatActivity {

    private String store_email;
    private String store_name;
    private String distributor_email;
    private String distributor_name;
    private String product_name;
    private double product_cost;
    private String product_img_url;
    private int product_units_per_package;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button button;
    private ImageView imageView;
    private TextView textView;
    private TextView textView_units;
    private EditText editText;
    private CircularProgressDrawable circularProgressDrawable;
    private boolean mailClientOpened = false;
    private int quantity;
    private double total_cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__order__product);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        store_email = sharedPreferences.getString("STORE_EMAIL", null);
        store_name = sharedPreferences.getString("STORE_NAME", null);

        distributor_email = getIntent().getStringExtra("DISTRIBUTOR_EMAIL");
        distributor_name = getIntent().getStringExtra("DISTRIBUTOR_NAME");
        product_cost = getIntent().getDoubleExtra("PRODUCT_COST", 0);
        product_name = getIntent().getStringExtra("PRODUCT_NAME");
        product_img_url = getIntent().getStringExtra("PRODUCT_IMG_URL");
        product_units_per_package = getIntent().getIntExtra("PRODUCT_UNITS_PER_PACKAGE", 0);

        //initialize views
        button = (Button) findViewById(R.id.buttonOrderSpecificProduct);
        imageView = (ImageView) findViewById(R.id.imageViewImageOfProduct);
        textView = (TextView) findViewById(R.id.textViewProductName1);
        textView_units = (TextView) findViewById(R.id.textViewUnitsPerPackage);
        editText = (EditText) findViewById(R.id.editTextQuantity);
        circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //set text view to product name
        textView.setText(product_name);
        textView_units.setText("one package contains " + String.valueOf(product_units_per_package) + " units");

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
                    showAlertDialog(STORE_ORDER_PRODUCT.this);
                }
            }
        });

        setProductPicture();
    }

    //getting picture of product
    private void setProductPicture()
    {
        //load pic into image view with url
        StorageReference storageReference = storage.getReferenceFromUrl(product_img_url);
        GlideApp.with(getApplicationContext())
                .load(storageReference)
                .fitCenter()
                .placeholder(circularProgressDrawable)
                .into(imageView);
    }

    //alert dialog
    private void showAlertDialog(final Context context)
    {
        total_cost = product_units_per_package * product_cost * quantity;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Place Order");
        // make text according to entered quantity
        if (quantity == 1) {
            alertDialog.setMessage("You want to order " + editText.getText().toString() + " box of product\n\n"
                    + "Total cost: " + String.valueOf(total_cost) + "$");
        }
        else if(quantity == 0) {
            alertDialog.setMessage("Please place an order of more that one product");
        }
        else {
            alertDialog.setMessage("You want to order " + editText.getText().toString() + " boxes of product\n\n"
                    + "Total cost: " + String.format("%.2f", total_cost) + "$");
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
                    orderProduct(getApplicationContext());
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

    /*private void sendMail(final Context context, final String name)
    {
        //get store details for email
        db.collection("Companies")
                .document(distributor_email)
                .collection("Stores")
                .document(store_email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Store store = documentSnapshot.toObject(Store.class);



                            //make email
                            String[] TO = {distributor_email};
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);

                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Order");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Store: " + store.get_name() + "\n" +
                                    "Order: " + String.valueOf(quantity) + " boxes of " + name + "\n\n" +
                                    "Total cost: " + String.valueOf(total_cost));

                            try {
                                //open email client
                                startActivityForResult(emailIntent, 1);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
            });
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
    }*/

    //log the order
    private void orderProduct(final Context context)
    {
        mailClientOpened = true;
        final Order order = new Order(Timestamp.now().toDate(), product_name, quantity, distributor_name, product_img_url, total_cost, store_email, store_name);

        db.collection("Companies")
                .document(distributor_email)
                .collection("Orders")
                .add(order)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        db.collection("Stores")
                                .document(store_email)
                                .collection("Orders")
                                .add(order)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            //send mail
                                            SendMail sendMail = new SendMail(STORE_ORDER_PRODUCT.this, distributor_email, "Product Order",
                                                    "Store: " + store_name + "\n" +
                                                            "Order: " + String.valueOf(quantity) + " boxes of " + product_name + "\n\n" +
                                                            "Total cost: " + String.valueOf(total_cost), 0);

                                            sendMail.execute();
                                        } else {
                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        //if user ordered
        if (mailClientOpened){
            startActivity(new Intent(STORE_ORDER_PRODUCT.this, STORE_MAIN_PAGE.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //if user opened email client flag is true
        mailClientOpened = true;
    }*/
}
