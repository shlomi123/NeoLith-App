package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

//TODO convert admin actions to be compatible with generic version of app
public class ADMIN_MAIN_PAGE extends AppCompatActivity /*implements StoreAdapter.OnItemClickListener*/{

    private Button delete;
    private Button addStore;
    private Button viewOrders;
    private Button addProduct;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Store> mStores;
    private String company_name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        company_name = sharedPreferences.getString("COMPANY_NAME", null);

        mRecyclerView = findViewById(R.id.recycler_view_stores_main);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_stores_main);
        mStores = new ArrayList<>();

        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            documentSnapshot = currentDocumentSnapshot;
                        }
                    }

                    String id = documentSnapshot.getId();

                    db.collection("Companies").document(id).collection("Stores").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Error with listener", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                {
                                    Store store = document.toObject(Store.class);
                                    mStores.add(store);
                                }

                                mAdapter = new StoreAdapter(ADMIN_MAIN_PAGE.this, mStores);

                                mRecyclerView.setAdapter(mAdapter);
                                //mAdapter.setOnItemClickListener(ADMIN_MAIN_PAGE.this);
                                mProgressCircle.setVisibility(View.INVISIBLE);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No Stores to Show", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        /*delete = (Button) findViewById(R.id.buttonDelete);
        addStore = (Button) findViewById(R.id.buttonAddStore);
        viewOrders = (Button) findViewById(R.id.buttonViewOrders);
        addProduct = (Button) findViewById(R.id.buttonAddProduct);
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_DELETE_STORE.class));
            }
        });

        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_ADD_STORE.class));
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_VIEW_STORES.class));
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_SCAN_PRODUCT.class));
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                firebaseAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("key-flag", false);
                editor.apply();
                Intent loginIntent = new Intent(ADMIN_MAIN_PAGE.this, LOGIN.class);
                startActivity(loginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onItemClick(int position)
    {
        Store store = mStores.get(position);
        Toast.makeText(getApplicationContext(), store.get_name(), Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(CLIENT_SHOW_ALL_PRODUCTS.this, CLIENT_ORDER_PRODUCT.class);
        intent.putExtra("NAME", product.getName());
        startActivity(intent);
    }

    @Override
    public void onViewStore(int position) {
        Toast.makeText(this, "view click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteStore(int position) {
        Toast.makeText(this, "Delete click at position: " + position, Toast.LENGTH_SHORT).show();
    }*/
}
