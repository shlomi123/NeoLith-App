package com.shlomi123.chocolith;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ADMIN_MAIN_PAGE extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int fragment_num = 1;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private String email;
    private String profile_path;
    private ImageView profile_picture;
    private CircularProgressDrawable circularProgressDrawable;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        user = mAuth.getCurrentUser();
        // company id
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        profile_path = sharedPreferences.getString("COMPANY_PROFILE", null);
        email = user.getEmail();


        //add custom toolbar
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);

        //drawer settings
        drawer = findViewById(R.id.main_page_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //add profile picture
        circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
        circularProgressDrawable.start();
        profile_picture = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        final StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                GlideApp.with(getApplicationContext())
                        .load(storageReference)
                        .fitCenter()
                        .signature(new ObjectKey(storageMetadata.getCreationTimeMillis()))
                        .placeholder(circularProgressDrawable)
                        .into(profile_picture);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new StoresFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_stores);
            getSupportActionBar().setTitle("Stores");
        }

        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission())
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        switch (fragment_num){
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new StoresFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_stores);
                getSupportActionBar().setTitle("Stores");
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new ProductsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_products);
                getSupportActionBar().setTitle("Products");
                break;
            case 3:
                final StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
                storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {

                        GlideApp.with(getApplicationContext())
                                .load(storageReference)
                                .fitCenter()
                                .signature(new ObjectKey(storageMetadata.getCreationTimeMillis()))
                                .placeholder(circularProgressDrawable)
                                .into(profile_picture);
                    }
                });

                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new StoresFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_stores);
                getSupportActionBar().setTitle("Stores");
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //item clicked in drawer menu
        switch (item.getItemId()) {
            case R.id.nav_stores:
                //open store fragment
                fragment_num = 1;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new StoresFragment()).commit();
                getSupportActionBar().setTitle("Stores");
                break;
            case R.id.nav_products:
                //open product fragment
                fragment_num = 2;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new ProductsFragment()).commit();
                getSupportActionBar().setTitle("Products");
                break;
            case R.id.nav_scan_feature:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new ScanFragment()).commit();
                getSupportActionBar().setTitle("Scan Feature");
                break;
            case R.id.nav_profile:
                //open edit profile fragment
                fragment_num = 3;
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, COMPANY_EDIT_PROFILE.class));
                break;
            case R.id.nav_sign_out:
                mAuth.signOut();
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, COMPANY_SIGN_IN.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
            case R.id.nav_feedback:
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, ADMIN_FEEDBACK.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
