package com.shlomi123.chocolith;

import android.content.Context;
import android.content.SharedPreferences;
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

public class CLIENT_ORDER_HISTORY extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;
    private ProgressBar mProgressCircle;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Order> mOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client__order__history);

        mRecyclerView = findViewById(R.id.recycler_view_order_history);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_order_history);

        mOrders = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID", null);

        if (id != null)
        {
            CollectionReference stores = db.collection("Stores");

            stores.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Store store = documentSnapshot.toObject(Store.class);

                        mOrders = Helper.getOrdersFromStore(store);

                        if (mOrders.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "No orders made", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        mAdapter = new OrderAdapter(CLIENT_ORDER_HISTORY.this, mOrders);

                        mRecyclerView.setAdapter(mAdapter);
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
        else
        {
            Toast.makeText(getApplicationContext(), "Error: no id", Toast.LENGTH_SHORT).show();
        }

    }
}
