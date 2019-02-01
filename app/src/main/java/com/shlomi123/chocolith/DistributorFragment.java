package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO show distributors that store is assigned to
public class DistributorFragment extends Fragment implements DistributorAdapter.OnItemClickListener{

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mRecyclerView;
    private DistributorAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Distributor> mDistributors;
    private List<String> IDS;
    private String store_email;
    private Button button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.distributor_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        store_email = sharedPreferences.getString("STORE_EMAIL", null);

        mProgressCircle = view.findViewById(R.id.progress_circle_distributors);
        //button = view.findViewById(R.id.button_add_distributor);

        mRecyclerView = view.findViewById(R.id.recycler_view_distributors);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDistributors = new ArrayList<>();
        //IDS = new ArrayList<>();

        mAdapter = new DistributorAdapter(getActivity(), mDistributors);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(DistributorFragment.this);

        db.collection("Stores")
                .document(store_email)
                .collection("Distributors")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            mDistributors.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Distributor distributor = documentSnapshot.toObject(Distributor.class);
                                mDistributors.add(distributor);
                            }

                            mAdapter.notifyDataSetChanged();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        } else {
                            mProgressCircle.setVisibility(View.INVISIBLE);
                            mDistributors.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Distributor distributor = mDistributors.get(position);
        Intent intent = new Intent(getActivity(), STORE_SHOW_DISTRIBUTOR_PRODUCTS.class);
        intent.putExtra("DISTRIBUTOR_ID", distributor.getId());
        intent.putExtra("DISTRIBUTOR_EMAIL", distributor.getEmail());
        intent.putExtra("DISTRIBUTOR_NAME", distributor.getName());
        startActivity(intent);
    }
}
