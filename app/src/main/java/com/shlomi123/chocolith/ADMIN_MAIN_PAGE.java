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
import android.widget.ImageButton;
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
public class ADMIN_MAIN_PAGE extends AppCompatActivity implements StoreAdapter.OnItemClickListener{

    private ImageButton addStore;
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
        addStore = (ImageButton) findViewById(R.id.buttonAddStore);
        mRecyclerView = findViewById(R.id.recycler_view_stores_main);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle_stores_main);
        mStores = new ArrayList<>();

        // get required company
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

                    //create a listener on store collection
                    db.collection("Companies").document(id).collection("Stores").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Error with listener", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // create recycler view to show stores and their data
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                mStores.clear();
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                {
                                    Store store = document.toObject(Store.class);
                                    mStores.add(store);
                                }

                                mAdapter = new StoreAdapter(ADMIN_MAIN_PAGE.this, mStores);

                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.setOnItemClickListener(ADMIN_MAIN_PAGE.this);
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

        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_ADD_STORE.class));
            }
        });
        /*addProduct.setOnClickListener(new View.OnClickListener() {
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

    //menu that shows option of signing out, create excel sheet, and add product
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO add option to create excel sheet and add a product
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

    @Override
    public void onItemClick(int position)
    {
        Toast.makeText(getApplicationContext(), "long press to show options", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewStore(int position) {
        Store chosenStore = mStores.get(position);

        // check first if orders is still null
        if (chosenStore.getOrders() != null)
        {
            // check if any orders were made
            if (!chosenStore.getOrders().isEmpty())
            {
                Intent intent = new Intent(ADMIN_MAIN_PAGE.this, ADMIN_VIEW_STORE_ORDERS.class);
                intent.putExtra("NAME", chosenStore.get_name());
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Orders Were Made", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Orders Were Made", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteStore(int position) {
        Store chosenStore = mStores.get(position);
        //TODO show a warning before deletion
        // delete chosen store
        deleteStore(chosenStore.get_name());
    }

    @Override
    protected void onPause(){
        super.onPause();
        //after leaving activity the list over stores needs to be emptied to prevent duplicates
        mStores.clear();
    }

    private void deleteStore(final String storeName) {
        // get required company
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
                            // this is the companies document
                            documentSnapshot = currentDocumentSnapshot;
                        }
                    }

                    final String id = documentSnapshot.getId();
                    // get the document of the store that needs to be deleted
                    db.collection("Companies").document(id).collection("Stores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = null;
                                for (DocumentSnapshot currentDocumentSnapshot : task.getResult()) {
                                    String name = currentDocumentSnapshot.getString("_name");
                                    if (name.equals(storeName)) {
                                        // this is the document that needs to be deleted
                                        documentSnapshot = currentDocumentSnapshot;
                                    }
                                }
                                // deletion....
                                db.collection("Companies")
                                        .document(id)
                                        .collection("Stores")
                                        .document(documentSnapshot.getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful())
                                                {
                                                    Toast.makeText(getApplicationContext(), "Error While Deleting", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

}
