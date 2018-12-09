package com.shlomi123.chocolith;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

//TODO convert admin actions to be compatible with generic version of app: sign out, add product, save excel sheet of one/all store/s
public class ADMIN_MAIN_PAGE extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int fragment_num = 1;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__main__page);

        //add custom toolbar
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);

        //drawer settings
        drawer = findViewById(R.id.main_page_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                break;
            case R.id.nav_excel:
                //TODO create excel sheet
                Toast.makeText(this, "excel", Toast.LENGTH_SHORT).show();
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

}
