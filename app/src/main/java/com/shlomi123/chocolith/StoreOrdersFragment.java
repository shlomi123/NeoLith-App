package com.shlomi123.chocolith;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreOrdersFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar mProgressCircle;
    private SharedPreferences sharedPreferences;
    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;
    private List<Order> mOrders;
    private String store_email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_orders_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        store_email = sharedPreferences.getString("STORE_EMAIL", null);
        mRecyclerView = view.findViewById(R.id.recycler_view_store_orders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle = view.findViewById(R.id.progress_circle_store_orders);
        mOrders = new ArrayList<>();
        mAdapter = new OrderAdapter(getContext(), mOrders);
        mRecyclerView.setAdapter(mAdapter);

        db.collection("Stores")
                .document(store_email)
                .collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            mOrders.clear();
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                Order order = documentSnapshot.toObject(Order.class);
                                mOrders.add(order);
                            }

                            if (mOrders.isEmpty())
                            {
                                Toast.makeText(getContext(), "No orders made", Toast.LENGTH_SHORT).show();
                            }

                            mAdapter.notifyDataSetChanged();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                            mAdapter.notifyDataSetChanged();
                            mOrders.clear();
                        }
                    }
                });
    }
}