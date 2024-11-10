package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.HomepageFragment;
import com.google.android.material.navigation.NavigationView;

public class HomepageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.navigationView);

        // Set up the toolbar as an action bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Add a toggle button for opening/closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        // Initialize NavigationView and set listener for menu items
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_login) {
                    // Open LoginActivity
                    Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_signup) {
                    Intent intent = new Intent(HomepageActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }

                // Close the drawer after an item is selected
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Load fragment into the container
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomepageFragment())
                    .commit();
        }
    }
}
