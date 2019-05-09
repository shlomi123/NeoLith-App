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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar mProgressCircle;
    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;
    private List<Order> mOrders;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();
        mRecyclerView = view.findViewById(R.id.recycler_distributor_view_store_orders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle = view.findViewById(R.id.progress_circle_distributor_store_orders);
        mOrders = new ArrayList<>();
        mAdapter = new OrderAdapter(getContext(), mOrders);
        mRecyclerView.setAdapter(mAdapter);

        db.collection("Companies")
                .document(email)
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

                            Collections.sort(mOrders, new Helper.sortOrdersByDate());

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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.distributor_orders_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_orders_sort_by_name:
                Collections.sort(mOrders, new Helper.sortOrdersByProductName());
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.item_orders_sort_by_cost:
                Collections.sort(mOrders, new Helper.sortOrdersByTotalCost());
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.item_orders_sort_by_date:
                Collections.sort(mOrders, new Helper.sortOrdersByDate());
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }
}
