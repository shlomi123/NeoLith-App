package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//TODO edit profile fragment
//TODO implement profile picture where needed
//TODO show distributor how to use scanning feature
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
    private TextView profile_name;
    private String name;
    private CircularProgressDrawable circularProgressDrawable;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        // company id
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("COMPANY_EMAIL", null);
        profile_path = sharedPreferences.getString("COMPANY_PROFILE", null);
        name = sharedPreferences.getString("COMPANY_NAME", null);
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
        StorageReference storageReference = storage.getReferenceFromUrl(profile_path);
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .fitCenter()
                .placeholder(circularProgressDrawable)
                .into(profile_picture);

        //add profile name
        profile_name = navigationView.getHeaderView(0).findViewById(R.id.profile_name);
        profile_name.setText(name);

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
            case R.id.nav_sign_out:
                mAuth.signOut();
                startActivity(new Intent(ADMIN_MAIN_PAGE.this, COMPANY_SIGN_IN.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
            case R.id.nav_excel:
                createExcelSheet();
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

    private void createExcelSheet(){
        db.collection("Companies")
                .document(email)
                .collection("Stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    // check that store name doesn't already exist
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Helper.saveExcelFile(getApplicationContext(), task.getResult().toObjects(Store.class)))
                            {
                                Toast.makeText(getApplicationContext(), "excel sheet created in downloads", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "error creating excel sheet", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
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

}
