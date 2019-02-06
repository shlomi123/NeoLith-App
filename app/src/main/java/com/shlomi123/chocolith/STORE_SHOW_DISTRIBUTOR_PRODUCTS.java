package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class STORE_SHOW_DISTRIBUTOR_PRODUCTS extends AppCompatActivity implements DistributorProductsAdapter.OnItemClickListener{

    private String distributor_email;
    private String distributor_name;
    private RecyclerView mRecyclerView;
    private DistributorProductsAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Product> mProducts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__show__distributor__products);

        distributor_email = getIntent().getStringExtra("DISTRIBUTOR_EMAIL");
        distributor_name = getIntent().getStringExtra("DISTRIBUTOR_NAME");

        mProgressCircle = findViewById(R.id.progress_circle_distributors_products);

        mRecyclerView = findViewById(R.id.recycler_view_distributors_products);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mProducts = new ArrayList<>();


        mAdapter = new DistributorProductsAdapter(getApplicationContext(), mProducts);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(STORE_SHOW_DISTRIBUTOR_PRODUCTS.this);

        // recycler view of distributors products
        db.collection("Companies")
                .document(distributor_email)
                .collection("Products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            mProducts.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Product product = documentSnapshot.toObject(Product.class);
                                mProducts.add(product);
                            }

                            mAdapter.notifyDataSetChanged();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        } else {
                            mProgressCircle.setVisibility(View.INVISIBLE);
                            mProducts.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Product product = mProducts.get(position);

        Intent intent = new Intent(STORE_SHOW_DISTRIBUTOR_PRODUCTS.this, STORE_ORDER_PRODUCT.class);
        intent.putExtra("DISTRIBUTOR_EMAIL", distributor_email);
        intent.putExtra("DISTRIBUTOR_NAME", distributor_name);
        intent.putExtra("PRODUCT_NAME", product.getName());
        intent.putExtra("PRODUCT_COST", product.getCost());
        intent.putExtra("PRODUCT_IMG_URL", product.getImageUrl());
        intent.putExtra("PRODUCT_UNITS_PER_PACKAGE", product.getUnits_per_package());

        startActivity(intent);
    }
}
