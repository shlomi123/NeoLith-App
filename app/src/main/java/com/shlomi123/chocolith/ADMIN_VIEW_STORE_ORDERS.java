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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ADMIN_VIEW_STORE_ORDERS extends AppCompatActivity implements OrderAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private String store_name;
    private String store_email;
    private SharedPreferences sharedPreferences;
    private String company_name;
    private String company_email;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Order> mOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__view__store__orders);

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        company_name = sharedPreferences.getString("COMPANY_NAME", null);
        company_email = sharedPreferences.getString("COMPANY_EMAIL", null);

        mRecyclerView = findViewById(R.id.recycler_view_orders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_orders);

        mOrders = new ArrayList<>();
        store_name = getIntent().getStringExtra("NAME");
        store_email = getIntent().getStringExtra("EMAIL");

        db.collection("Stores")
                .document(store_email)
                .collection("Orders")
                .whereEqualTo("_distributor", company_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            mOrders.add(documentSnapshot.toObject(Order.class));
                        }

                        mAdapter = new OrderAdapter(ADMIN_VIEW_STORE_ORDERS.this, mOrders);

                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(ADMIN_VIEW_STORE_ORDERS.this);
                        mProgressCircle.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*final CollectionReference companies = db.collection("Companies");

        //find the document of required company
        companies.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = null;
                    for (DocumentSnapshot currentDocumentSnapshot : task.getResult())
                    {
                        String name = currentDocumentSnapshot.getString("Name");
                        if (name.equals(company_name))
                        {
                            // this is the companies document
                            documentSnapshot = currentDocumentSnapshot;
                        }
                    }

                    String id = documentSnapshot.getId();

                    //find the document of the store that needs to be viewed
                    companies.document(id).collection("Stores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot documentSnapshot: task.getResult())
                                {
                                    //this is the document of the corresponding document
                                    Store store = documentSnapshot.toObject(Store.class);
                                    if (store.get_name().equals(Name))
                                    {
                                        // add stores orders to the order list
                                        mOrders = Helper.getOrdersFromStore(store);
                                    }

                                }

                                // create recycler view
                                mAdapter = new OrderAdapter(ADMIN_VIEW_STORE_ORDERS.this, mOrders);

                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.setOnItemClickListener(ADMIN_VIEW_STORE_ORDERS.this);
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
                    Toast.makeText(getApplicationContext(), "Error getting company", Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            }
        });*/
    }

    @Override
    public void onItemClick(int position)
    {
        Order order = mOrders.get(position);
        Toast.makeText(getApplicationContext(), order.get_product(), Toast.LENGTH_SHORT).show();
    }
}
