package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StoresFragment extends Fragment implements StoreAdapter.OnItemClickListener {

    private ImageButton addStore;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Store> mStores;
    private String id;
    private String company_email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stores_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("COMPANY_ID", null);
        company_email = sharedPreferences.getString("COMPANY_EMAIL", null);
        addStore = (ImageButton) view.findViewById(R.id.button_add_store);
        mRecyclerView = view.findViewById(R.id.recycler_view_stores_main);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle = view.findViewById(R.id.progress_circle_stores_main);
        mStores = new ArrayList<>();

        mAdapter = new StoreAdapter(getActivity(), mStores);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(StoresFragment.this);

        // get required company
        db.collection("Companies")
                .document(id)
                .collection("Stores")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        // create recycler view to show stores and their data
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            mStores.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Store store = document.toObject(Store.class);
                                mStores.add(store);
                            }

                            mAdapter.notifyDataSetChanged();

                            mProgressCircle.setVisibility(View.INVISIBLE);
                        } else {
                            mProgressCircle.setVisibility(View.INVISIBLE);
                            mStores.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ADMIN_ADD_STORE.class));
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "long press to show options", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewStore(int position) {
        Store chosenStore = mStores.get(position);

        // check first if orders is still null
        if (chosenStore.getOrders() != null) {
            // check if any orders were made
            if (!chosenStore.getOrders().isEmpty()) {
                Intent intent = new Intent(getActivity(), ADMIN_VIEW_STORE_ORDERS.class);
                intent.putExtra("NAME", chosenStore.get_name());
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "No Orders Were Made", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No Orders Were Made", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteStore(int position) {
        Store chosenStore = mStores.get(position);
        //TODO show a warning before deletion
        // delete chosen store
        deleteStore(chosenStore.get_name(), chosenStore.get_email());
    }

    private void deleteStore(final String storeName, final String storeEmail) {
        // get required company
        //TODO what if store is in the middle of ordering

        db.collection("Companies")
                .document(id)
                .collection("Stores")
                .document(storeEmail)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error While Deleting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        db.collection("Stores")
                .document(storeEmail)
                .collection("Distributors")
                .document(company_email)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error While Deleting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
