package com.example.eventplanner.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.homepage.HomepageCardsFragment;
import com.example.eventplanner.fragments.homepage.HomepageFilterFragment;
import com.example.eventplanner.fragments.homepage.HomepageProductsServicesFragment;
import com.example.eventplanner.fragments.servicecreation.ServiceManagement;
import com.google.android.material.navigation.NavigationView;

public class OrganiserHomepageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_homepage);

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


                if (id == R.id.nav_home) {
                    Intent intent = new Intent(OrganiserHomepageActivity.this, OrganiserHomepageActivity.class);
                    startActivity(intent);
                }


                else if(id==R.id.nav_services){
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.homepage_fragment_container, new ServiceManagement())
                            .commit();

                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentById(R.id.cards_fragment_container))
                            .commit();
                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentById(R.id.filter_fragment_container))
                            .commit();
                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentById(R.id.cards_products_fragment_container))
                            .commit();
                    fragmentManager.beginTransaction()
                            .remove(fragmentManager.findFragmentById(R.id.filter_fragment_container_products))
                            .commit();
                }


                else if (id == R.id.nav_view_profile) {
                    Intent intent = new Intent(OrganiserHomepageActivity.this, ProfileViewActivity.class);
                    startActivity(intent);
                }


                else if (id == R.id.nav_log_out) {
                    logOut();
                }

                // Close the drawer after an item is selected
                drawerLayout.closeDrawers();
                return true;
            }
        });


        // Load fragments into the respective containers
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.cards_fragment_container, new HomepageCardsFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.filter_fragment_container, new HomepageFilterFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.cards_products_fragment_container, new HomepageProductsServicesFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.filter_fragment_container_products, new HomepageFilterFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.organiser_menu, menu);

        MenuCompat.setGroupDividerEnabled(menu, true);

        return true;
    }


    private void logOut() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Log out?");
        dialog.setMessage("Are you sure you want to log out?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(OrganiserHomepageActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }
}
