package com.shlomi123.chocolith;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

//TODO show distributors that store is assigned to
public class DistributorFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private RecyclerView mRecyclerView;
    private StoreAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Distributor> mDistriutor;
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

    }
}
