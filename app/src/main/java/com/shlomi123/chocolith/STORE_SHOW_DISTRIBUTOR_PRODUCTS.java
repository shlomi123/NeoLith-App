package com.shlomi123.chocolith;

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

    private String distributor_id;
    private String distributor_email;
    private RecyclerView mRecyclerView;
    private DistributorProductsAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Product> mProducts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store__show__distributor__products);

        distributor_id = getIntent().getStringExtra("DISTRIBUTOR_ID");
        distributor_email = getIntent().getStringExtra("DISTRIBUTOR_EMAIL");

        mProgressCircle = findViewById(R.id.progress_circle_distributors_products);

        mRecyclerView = findViewById(R.id.recycler_view_distributors_products);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mProducts = new ArrayList<>();


        mAdapter = new DistributorProductsAdapter(getApplicationContext(), mProducts);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(STORE_SHOW_DISTRIBUTOR_PRODUCTS.this);

        db.collection("Companies")
                .document(distributor_id)
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
        //TODO order product
    }
}
