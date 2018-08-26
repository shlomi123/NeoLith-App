package com.shlomi123.chocolith;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ADMIN_VIEW_STORES extends AppCompatActivity /*implements StoreAdapter.OnItemClickListener*/{

    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Store> mStores;
    private List<Store> mStores1;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__view__stores);

        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission())
            {
                requestPermission();
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        mRecyclerView = findViewById(R.id.recycler_view_stores);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_stores);
        button = findViewById(R.id.buttonCreateExcelSheetStores);

        mStores = new ArrayList<>();
        mStores1 = new ArrayList<>();

        final CollectionReference stores = db.collection("Stores");

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stores.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot documentSnapshot: task.getResult())
                            {
                                Store store = documentSnapshot.toObject(Store.class);
                                mStores1.add(store);
                            }
                            if (Helper.saveExcelFile(getApplicationContext(), mStores))
                            {
                                Toast.makeText(getApplicationContext(), "Successfully created excel", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Error getting stores", Toast.LENGTH_SHORT).show();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
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
