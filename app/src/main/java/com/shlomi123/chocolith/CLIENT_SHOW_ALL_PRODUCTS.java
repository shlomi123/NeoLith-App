package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CLIENT_SHOW_ALL_PRODUCTS extends AppCompatActivity /*implements ImageAdapter.OnItemClickListener*/{
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;

    private ProgressBar mProgressCircle;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Product> mProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__show__all__products);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);

        mProducts = new ArrayList<>();

        CollectionReference products = db.collection("Products");

        products.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Product product = documentSnapshot.toObject(Product.class);
                        mProducts.add(product);
                    }

                    mAdapter = new ProductAdapter(CLIENT_SHOW_ALL_PRODUCTS.this, mProducts);

                    mRecyclerView.setAdapter(mAdapter);
                    //mAdapter.setOnItemClickListener(CLIENT_SHOW_ALL_PRODUCTS.this);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error getting product", Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /*@Override
    public void onItemClick(int position)
    {
        Product product = mProducts.get(position);
        Intent intent = new Intent(CLIENT_SHOW_ALL_PRODUCTS.this, CLIENT_ORDER_PRODUCT.class);
        intent.putExtra("NAME", product.getName());
        startActivity(intent);
    }*/
}
