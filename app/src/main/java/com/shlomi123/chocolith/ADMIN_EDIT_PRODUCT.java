package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class ADMIN_EDIT_PRODUCT extends AppCompatActivity {

    private ImageView edit4;
    private ImageView edit3;
    private ImageView edit2;
    private ImageView check2;
    private ImageView check3;
    private ImageView check4;
    private TextView product_name;
    private EditText edit_product_name;
    private TextView price_per_unit;
    private EditText edit_price_per_unit;
    private TextView units_per_package;
    private EditText edit_units_per_package;
    private ProgressBar progress_price;
    private ProgressBar progress_name;
    private ProgressBar progress_units;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__edit__product);

        Gson gson = new Gson();
        final Product product = gson.fromJson(getIntent().getStringExtra("JSON"), Product.class);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("COMPANY_EMAIL", null);

        edit4 = (ImageView) findViewById(R.id.imageView_edit_4);
        edit3 = (ImageView) findViewById(R.id.imageView_edit_3);
        edit2 = (ImageView) findViewById(R.id.imageView_edit_2);
        check2 = (ImageView) findViewById(R.id.imageView_check_2);
        check3 = (ImageView) findViewById(R.id.imageView_check_3);
        check4 = (ImageView) findViewById(R.id.imageView_check_4);
        progress_name = (ProgressBar) findViewById(R.id.progressBar_update_product_name);
        progress_price = (ProgressBar) findViewById(R.id.progressBar_update_price_per_unit);
        progress_units = (ProgressBar) findViewById(R.id.progressBar_update_units_per_package);
        product_name = (TextView) findViewById(R.id.textView_edit_product_name);
        edit_product_name = (EditText) findViewById(R.id.editText_edit_product_name);
        price_per_unit = (TextView) findViewById(R.id.textView_price_per_unit);
        edit_price_per_unit = (EditText) findViewById(R.id.editText_price_per_unit);
        units_per_package = (TextView) findViewById(R.id.textView_units_per_package);
        edit_units_per_package = (EditText) findViewById(R.id.editText_units_per_package);

        product_name.setText(product.getName());
        edit_product_name.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);
        progress_name.setVisibility(View.INVISIBLE);

        price_per_unit.setText(String.valueOf(product.getCost()));
        edit_price_per_unit.setVisibility(View.INVISIBLE);
        check3.setVisibility(View.INVISIBLE);
        progress_price.setVisibility(View.INVISIBLE);

        units_per_package.setText(String.valueOf(product.getUnits_per_package()));
        edit_units_per_package.setVisibility(View.INVISIBLE);
        check4.setVisibility(View.INVISIBLE);
        progress_units.setVisibility(View.INVISIBLE);

        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_name.setVisibility(View.INVISIBLE);
                edit_product_name.setVisibility(View.VISIBLE);
                edit_product_name.setText("");
                edit4.setVisibility(View.INVISIBLE);
                check4.setVisibility(View.VISIBLE);
            }
        });

        check4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_product_name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"please enter something", Toast.LENGTH_SHORT).show();
                }else{
                    progress_name.setVisibility(View.VISIBLE);
                    check4.setVisibility(View.INVISIBLE);

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    db.collection("Companies")
                            .document(email)
                            .collection("Products")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Product currProduct = documentSnapshot.toObject(Product.class);

                                        if (currProduct.getName().equals(product_name.getText().toString())) {
                                            db.collection("Companies")
                                                    .document(email)
                                                    .collection("Products")
                                                    .document(documentSnapshot.getId())
                                                    .update("name", edit_product_name.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            product_name.setVisibility(View.VISIBLE);
                                                            edit_product_name.setVisibility(View.INVISIBLE);
                                                            edit4.setVisibility(View.VISIBLE);
                                                            progress_name.setVisibility(View.INVISIBLE);
                                                            product_name.setText(edit_product_name.getText().toString());
                                                            Toast.makeText(getApplicationContext(), "succesfully upated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }
        });

        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                units_per_package.setVisibility(View.INVISIBLE);
                edit_units_per_package.setVisibility(View.VISIBLE);
                edit_units_per_package.setText("");
                edit3.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.VISIBLE);
            }
        });

        check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_units_per_package.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"please enter something", Toast.LENGTH_SHORT).show();
                }else{
                    progress_units.setVisibility(View.VISIBLE);
                    check3.setVisibility(View.INVISIBLE);

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    db.collection("Companies")
                            .document(email)
                            .collection("Products")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Product currProduct = documentSnapshot.toObject(Product.class);

                                        if (currProduct.getUnits_per_package() == Integer.parseInt(units_per_package.getText().toString())) {
                                            db.collection("Companies")
                                                    .document(email)
                                                    .collection("Products")
                                                    .document(documentSnapshot.getId())
                                                    .update("units_per_package", Integer.parseInt(edit_units_per_package.getText().toString()))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            units_per_package.setVisibility(View.VISIBLE);
                                                            edit_units_per_package.setVisibility(View.INVISIBLE);
                                                            edit3.setVisibility(View.VISIBLE);
                                                            check3.setVisibility(View.INVISIBLE);
                                                            units_per_package.setText(edit_units_per_package.getText().toString());
                                                            progress_units.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(), "succesfully upated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }
        });

        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                price_per_unit.setVisibility(View.INVISIBLE);
                edit_price_per_unit.setVisibility(View.VISIBLE);
                edit_price_per_unit.setText("");
                edit2.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.VISIBLE);
            }
        });

        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_price_per_unit.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"please enter something", Toast.LENGTH_SHORT).show();
                }else{
                    progress_price.setVisibility(View.VISIBLE);
                    check2.setVisibility(View.INVISIBLE);

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    db.collection("Companies")
                            .document(email)
                            .collection("Products")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Product currProduct = documentSnapshot.toObject(Product.class);

                                        if (currProduct.getCost() == Integer.parseInt(price_per_unit.getText().toString())) {
                                            db.collection("Companies")
                                                    .document(email)
                                                    .collection("Products")
                                                    .document(documentSnapshot.getId())
                                                    .update("cost", Integer.parseInt(edit_price_per_unit.getText().toString()))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            price_per_unit.setVisibility(View.VISIBLE);
                                                            edit_price_per_unit.setVisibility(View.INVISIBLE);
                                                            edit2.setVisibility(View.VISIBLE);
                                                            check2.setVisibility(View.INVISIBLE);
                                                            price_per_unit.setText(edit_price_per_unit.getText().toString());
                                                            progress_price.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(), "succesfully upated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
