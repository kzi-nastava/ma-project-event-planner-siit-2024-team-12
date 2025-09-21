package com.example.eventplanner.activities.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.eventplanner.fragments.categories.CategoriesContainerFragment;
import com.example.eventplanner.fragments.comment.CommentManagementFragment;
import com.example.eventplanner.fragments.event.InvitedEventsListFragment;
import com.example.eventplanner.fragments.homepage.EventListFragment;
import com.example.eventplanner.fragments.homepage.TopEventsFragment;
import com.example.eventplanner.fragments.homepage.TopSolutionsFragment;
import com.example.eventplanner.fragments.homepage.SolutionListFragment;
import com.example.eventplanner.fragments.notification.NotificationFragment;
import com.example.eventplanner.fragments.profile.SuspendedUserFragment;
import com.example.eventplanner.fragments.report.ReportManagementFragment;
import com.example.eventplanner.fragments.servicecreation.ServiceManagement;
import com.google.android.material.navigation.NavigationView;

public class HomepageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView chatRecyclerView;
    private NavigationView navigationView;
    private static final String TAG = "HomepageDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Log.d(TAG, "onCreate: HomepageActivity created.");

        drawerLayout = findViewById(R.id.navigationView);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isSuspended = sp.getBoolean("isSuspended", false);

        if (savedInstanceState == null) {
            if (isSuspended) {
                setupGuestUI();
                loadSuspendedFragment();
            } else {
                String role = sp.getString("userRole", null);
                if ("ROLE_ORGANIZER".equals(role)) {
                    setupOrganizerUI();
                } else if ("ROLE_PROVIDER".equals(role)) {
                    setupProviderUI();
                } else if ("ROLE_ADMIN".equals(role)) {
                    setupAdminUI();
                } else if ("ROLE_AUTHENTICATED_USER".equals(role)) {
                    setupAuthenticatedUserUI();
                } else {
                    setupGuestUI();
                }
                loadHomepageFragments();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (sp.getBoolean("isSuspended", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("isSuspended");
            editor.apply();
        }
    }

    private void loadHomepageFragments() {
        Log.d(TAG, "loadHomepageFragments: Attempting to load homepage fragments.");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.cards_fragment_container, new TopEventsFragment());
        transaction.replace(R.id.events_list_fragment_container, new EventListFragment());
        transaction.replace(R.id.cards_products_fragment_container, new TopSolutionsFragment());
        transaction.replace(R.id.ps_list_fragment_container, new SolutionListFragment());

        transaction.commitAllowingStateLoss();
        Log.d(TAG, "loadHomepageFragments: Fragments committed.");
    }

    private void showMainUI() {
        Log.d(TAG, "showMainUI: Restarting HomepageActivity to reset UI.");
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToFragment(int containerId, Fragment fragment) {
        Log.d(TAG, "navigateToFragment: Navigating to new fragment. Container ID: " + getResources().getResourceEntryName(containerId));

        View homepageScrollView = findViewById(R.id.homepage_scroll_view);
        if (homepageScrollView != null) homepageScrollView.setVisibility(View.GONE);

        View invitedEventsContainer = findViewById(R.id.invited_events_container);
        if (invitedEventsContainer != null) invitedEventsContainer.setVisibility(View.GONE);

        View notificationsContainer = findViewById(R.id.notifications_container);
        if (notificationsContainer != null) notificationsContainer.setVisibility(View.GONE);

        View suspendedFragmentContainer = findViewById(R.id.suspended_fragment_container);
        if (suspendedFragmentContainer != null) suspendedFragmentContainer.setVisibility(View.GONE);

        View homepageFragmentContainer = findViewById(R.id.homepage_fragment_container);
        if (homepageFragmentContainer != null) homepageFragmentContainer.setVisibility(View.GONE);


        View targetContainer = findViewById(containerId);
        if (targetContainer != null) {
            targetContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(containerId, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            Log.e(TAG, "navigateToFragment: Target container is NULL for ID: " + getResources().getResourceEntryName(containerId));
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
            } else if (id == R.id.nav_home) {
                showMainUI();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupAuthenticatedUserUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.authenticated_user_menu);

        RecyclerView chatRecyclerView = findViewById(R.id.chat_recycler_view);
        if (chatRecyclerView != null) chatRecyclerView.setVisibility(View.VISIBLE);

        Spinner userSpinner = findViewById(R.id.userSpinner);
        if (userSpinner != null) userSpinner.setVisibility(View.VISIBLE);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_view_profile) {
                startActivity(new Intent(HomepageActivity.this, ProfileViewActivity.class));
            } else if (id == R.id.nav_log_out) {
                logOut();
            } else if (id == R.id.nav_invited_events) {
                navigateToFragment(R.id.invited_events_container, new InvitedEventsListFragment());
            } else if (id == R.id.nav_calendar_od) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (id == R.id.nav_explore_events) {
                startActivity(new Intent(this, ExplorePageActivity.class));
            } else if (id == R.id.nav_notifications) {
                navigateToFragment(R.id.notifications_container, new NotificationFragment());
            } else if (id == R.id.nav_home) {
                showMainUI();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupOrganizerUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.organiser_menu);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showMainUI();
            } else if (id == R.id.nav_fav_events) {
                startActivity(new Intent(this, FavoriteEventsActivity.class));
            } else if (id == R.id.nav_services) {
                navigateToFragment(R.id.homepage_fragment_container, new ServiceManagement());
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
            } else if (id == R.id.nav_notifications) {
                navigateToFragment(R.id.notifications_container, new NotificationFragment());
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
                showMainUI();
            } else if (id == R.id.nav_create_business) {
                startActivity(new Intent(this, BusinessRegistrationActivity.class));
            } else if (id == R.id.nav_business_info) {
                startActivity(new Intent(this, BusinessInfoActivity.class));
            } else if (id == R.id.nav_products) {
                startActivity(new Intent(this, ProvidedProductsActivity.class));
            } else if (id == R.id.nav_services) {
                navigateToFragment(R.id.homepage_fragment_container, new ServiceManagement());
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
            } else if (id == R.id.nav_notifications) {
                navigateToFragment(R.id.notifications_container, new NotificationFragment());
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
                showMainUI();
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
            } else if (id == R.id.nav_notifications) {
                navigateToFragment(R.id.notifications_container, new NotificationFragment());
            } else if(id == R.id.nav_categories){
                navigateToFragment(R.id.notifications_container, new CategoriesContainerFragment());
            } else if (id == R.id.nav_manage_comments) {
                navigateToFragment(R.id.notifications_container, new CommentManagementFragment());
            } else if (id == R.id.nav_manage_reports) {
                navigateToFragment(R.id.notifications_container, new ReportManagementFragment());
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
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("userRole");
                    editor.apply();

                    Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void loadSuspendedFragment() {
        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String days = sp.getString("suspensionDays", "0");
        String hours = sp.getString("suspensionHours", "0");
        String minutes = sp.getString("suspensionMinutes", "0");

        Bundle args = new Bundle();
        args.putString("days", days);
        args.putString("hours", hours);
        args.putString("minutes", minutes);

        SuspendedUserFragment suspendedFragment = new SuspendedUserFragment();
        suspendedFragment.setArguments(args);

        navigateToFragment(R.id.suspended_fragment_container, suspendedFragment);
    }
}