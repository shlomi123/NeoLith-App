package com.shlomi123.chocolith;

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

public class ADMIN_VIEW_STORES extends AppCompatActivity /*implements StoreAdapter.OnItemClickListener*/{

    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Store> mStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__view__stores);

        mRecyclerView = findViewById(R.id.recycler_view_stores);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_stores);

        mStores = new ArrayList<>();

        CollectionReference stores = db.collection("Stores");

        stores.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Store store = documentSnapshot.toObject(Store.class);
                        mStores.add(store);
                    }

                    mAdapter = new StoreAdapter(ADMIN_VIEW_STORES.this, mStores);

                    mRecyclerView.setAdapter(mAdapter);
                    //mAdapter.setOnItemClickListener(ADMIN_VIEW_STORES.this);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error getting stores", Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /*@Override
    public void onItemClick(int position)
    {
        Store store = mStores.get(position);
        Toast.makeText(getApplicationContext(), store.get_name(), Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(CLIENT_SHOW_ALL_PRODUCTS.this, CLIENT_ORDER_PRODUCT.class);
        intent.putExtra("NAME", product.getName());
        startActivity(intent);
    }*/
}
