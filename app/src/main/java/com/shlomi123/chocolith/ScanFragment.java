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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ScanFragment extends Fragment {

    private String code;
    private TextView copyableText;
    private SharedPreferences sharedPreferences;
    private String company_email;
    private String company_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scan_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        company_email = sharedPreferences.getString("COMPANY_EMAIL", null);
        company_name = sharedPreferences.getString("COMPANY_NAME", null);


        code = company_name + "##" + company_email;

        copyableText = (TextView) getActivity().findViewById(R.id.textView_text_to_copy);
        copyableText.setText(code);
    }
}
