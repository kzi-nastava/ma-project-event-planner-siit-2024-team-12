package com.example.eventplanner.activities.homepage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.auth.LoginActivity;
import com.example.eventplanner.activities.auth.SignUpActivity;
import com.example.eventplanner.activities.business.BusinessInfoActivity;
import com.example.eventplanner.activities.business.BusinessRegistrationActivity;
import com.example.eventplanner.activities.calendar.CalendarActivity;
import com.example.eventplanner.activities.charts.AttendanceChart;
import com.example.eventplanner.activities.charts.RatingsChart;
import com.example.eventplanner.activities.eventtype.EventTypeCreationActivity;
import com.example.eventplanner.activities.eventtype.EventTypeTableActivity;
import com.example.eventplanner.activities.favorites.ExplorePageActivity;
import com.example.eventplanner.activities.favorites.FavoriteEventsActivity;
import com.example.eventplanner.activities.favorites.FavoriteProductsActivity;
import com.example.eventplanner.activities.favorites.FavoriteServicesActivity;
import com.example.eventplanner.activities.product.ProvidedProductsActivity;
import com.example.eventplanner.activities.profile.ProfileViewActivity;
import com.example.eventplanner.activities.event.EventCreationActivity;
import com.example.eventplanner.activities.solutioncategory.CategoriesTableActivity;
import com.example.eventplanner.adapters.ChatAdapter;
import com.example.eventplanner.fragments.homepage.EventListFragment;
import com.example.eventplanner.fragments.homepage.HomepageCardsFragment;
import com.example.eventplanner.fragments.homepage.HomepageFilterFragment;
import com.example.eventplanner.fragments.homepage.HomepageProductsServicesFragment;
import com.example.eventplanner.fragments.homepage.PSListFragment;
import com.example.eventplanner.fragments.servicecreation.ServiceManagement;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView chatRecyclerView;
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
        toolbar.setOverflowIcon(null);

        // Add toggle button for opening/closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize NavigationView
        navigationView = findViewById(R.id.nav_view);

        // Load homepage fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.cards_fragment_container, new HomepageCardsFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.filter_fragment_container, new HomepageFilterFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.events_list_fragment_container, new EventListFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.cards_products_fragment_container, new HomepageProductsServicesFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.filter_fragment_container_products, new HomepageFilterFragment())
                    .commit();

            fragmentManager.beginTransaction()
                    .replace(R.id.ps_list_fragment_container, new PSListFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = sp.getString("userRole", null);

        if ("ROLE_ORGANIZER".equals(role)) {
            setupOrganizerUI();
        } else if ("ROLE_PROVIDER".equals(role)) {
            setupProviderUI();
        } else if ("ROLE_ADMIN".equals(role)) {
            setupAdminUI();
        } else {
            setupGuestUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    private void setupGuestUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu);

        RecyclerView chatRecyclerView = findViewById(R.id.chat_recycler_view);
        if (chatRecyclerView != null) chatRecyclerView.setVisibility(View.GONE);

        Spinner userSpinner = findViewById(R.id.userSpinner);
        if (userSpinner != null) userSpinner.setVisibility(View.GONE);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
            } else if (id == R.id.nav_signup) {
                startActivity(new Intent(HomepageActivity.this, SignUpActivity.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cards_fragment_container, new HomepageCardsFragment())
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.filter_fragment_container, new HomepageFilterFragment())
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.events_list_fragment_container, new EventListFragment())
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.cards_products_fragment_container, new HomepageProductsServicesFragment())
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.filter_fragment_container_products, new HomepageFilterFragment())
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.ps_list_fragment_container, new PSListFragment())
                .commit();

    }

    private void setupOrganizerUI() {

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.organiser_menu);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomepageActivity.class));
            } else if (id == R.id.nav_fav_events) {
                startActivity(new Intent(this, FavoriteEventsActivity.class));
            } else if (id == R.id.nav_services) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.homepage_fragment_container, new ServiceManagement())
                        .commit();
            } else if (id == R.id.nav_view_profile) {
                startActivity(new Intent(this, ProfileViewActivity.class));
            } else if (id == R.id.nav_calendar_od) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (id == R.id.nav_create_event) {
                startActivity(new Intent(this, EventCreationActivity.class));
            } else if (id == R.id.nav_explore_events) {
                startActivity(new Intent(this, ExplorePageActivity.class));
            } else if (id == R.id.nav_fav_services) {
                startActivity(new Intent(this, FavoriteServicesActivity.class));
            } else if (id == R.id.nav_fav_products) {
                startActivity(new Intent(this, FavoriteProductsActivity.class));
            } else if (id == R.id.nav_log_out) {
                logOut();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupProviderUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.provider_menu);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
            } else if (id == R.id.nav_create_business) {
                startActivity(new Intent(this, BusinessRegistrationActivity.class));
            } else if (id == R.id.nav_business_info) {
                startActivity(new Intent(this, BusinessInfoActivity.class));
            } else if (id == R.id.nav_products) {
                startActivity(new Intent(this, ProvidedProductsActivity.class));
            } else if (id == R.id.nav_services) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.homepage_fragment_container, new ServiceManagement())
                        .commit();
            } else if (id == R.id.nav_calendar_od) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (id == R.id.nav_fav_events) {
                startActivity(new Intent(this, FavoriteEventsActivity.class));
            } else if (id == R.id.nav_fav_services) {
                startActivity(new Intent(this, FavoriteServicesActivity.class));
            } else if (id == R.id.nav_fav_products) {
                startActivity(new Intent(this, FavoriteProductsActivity.class));
            } else if (id == R.id.nav_explore_events) {
                startActivity(new Intent(this, ExplorePageActivity.class));
            } else if (id == R.id.nav_view_profile) {
                startActivity(new Intent(this, ProfileViewActivity.class));
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesTableActivity.class));
            } else if (id == R.id.nav_event_types) {
                startActivity(new Intent(this, EventTypeTableActivity.class));
            } else if (id == R.id.nav_log_out) {
                logOut();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }


    private void setupAdminUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.admin_menu);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
            } else if (id == R.id.nav_create_event_type) {
                startActivity(new Intent(this, EventTypeCreationActivity.class));
            } else if (id == R.id.nav_event_types_overview) {
                startActivity(new Intent(this, EventTypeTableActivity.class));
            } else if (id == R.id.nav_calendar_od) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (id == R.id.nav_view_profile) {
                startActivity(new Intent(this, ProfileViewActivity.class));
            } else if (id == R.id.nav_attendance_chart) {
                startActivity(new Intent(this, AttendanceChart.class));
            } else if (id == R.id.nav_ratings_chart) {
                startActivity(new Intent(this, RatingsChart.class));
            } else if (id == R.id.nav_log_out) {
                logOut();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }



    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle("Log out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("YES", (dialog, which) -> {
                    // Clear shared prefs
                    SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    sp.edit().clear().apply();

                    setupGuestUI();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
