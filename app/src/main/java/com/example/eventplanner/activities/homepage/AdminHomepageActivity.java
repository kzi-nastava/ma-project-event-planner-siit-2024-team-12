package com.example.eventplanner.activities.homepage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.profile.ProfileViewActivity;
import com.example.eventplanner.activities.event.eventtype.EventTypeCreationActivity;
import com.example.eventplanner.adapters.CategoryAdapter;
import com.example.eventplanner.adapters.ChatAdapter;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.fragments.homepage.EventListFragment;
import com.example.eventplanner.fragments.homepage.HomepageCardsFragment;
import com.example.eventplanner.fragments.homepage.HomepageFilterFragment;
import com.example.eventplanner.fragments.homepage.HomepageProductsServicesFragment;
import com.example.eventplanner.fragments.homepage.PSListFragment;
import com.example.eventplanner.model.Category;
import com.example.eventplanner.model.EventType;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminHomepageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView chatRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout);

        // Set up the toolbar as an action bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Add a toggle button for opening/closing the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up the RecyclerView for chat messages
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add messages to the chat
        List<String> messages = new ArrayList<>();
        messages.add("Hello, I purchased two tickets for the event, but I now need three. Is there any way to add one more to my order?!");
        messages.add("Hi, I’m interested in joining your event, but I have a few questions about the schedule. Could you provide more details?");
        messages.add("The venue looks amazing in the pictures! Will there be parking available nearby, or should I use public transport?");

        // Set the adapter with messages
        chatRecyclerView.setAdapter(new ChatAdapter(messages));

        // Scroll to the last message
        chatRecyclerView.scrollToPosition(messages.size() - 1);

        // Reference to the Spinner
        Spinner userSpinner = findViewById(R.id.userSpinner);


        List<String> users = new ArrayList<>();
        users.add("John Doe");
        users.add("Jane Smith");
        users.add("Event Coordinator");
        users.add("Admin");

        // Creating an adaptet for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        userSpinner.setAdapter(adapter);



        // table that displays solution categories offered by the current business
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setVisibility(View.GONE);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Category A", "Description A", "Active"));
        categories.add(new Category("2", "Category B", "Description B", "Inactive"));
        categories.add(new Category("3", "Category C", "Description C", "Active"));

        CategoryAdapter adapter2 = new CategoryAdapter(categories);
        categoryRecyclerView.setAdapter(adapter2);


        // table that displays event types of the current business
        RecyclerView eventTypecyclerView = findViewById(R.id.eventTypeRecyclerView);
        eventTypecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventTypecyclerView.setVisibility(View.GONE);

        List<EventType> events = new ArrayList<>();
        events.add(new EventType("1", "Concert", "Active"));
        events.add(new EventType("2", "Meeting", "Inactive"));

        EventTypeAdapter adapter3 = new EventTypeAdapter(events);
        eventTypecyclerView.setAdapter(adapter3);



        // Initialize NavigationView and set listener for menu items
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(AdminHomepageActivity.this, AdminHomepageActivity.class);
                    startActivity(intent);
                }

                else if (id == R.id.nav_create_event_type) {
                    Intent intent = new Intent(AdminHomepageActivity.this, EventTypeCreationActivity.class);
                    startActivity(intent);
                }

                else if (id == R.id.nav_event_types_overview) {
                    RecyclerView recyclerView = findViewById(R.id.eventTypeRecyclerView);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                else if (id == R.id.nav_calendar_od) {
                    Intent intent = new Intent(AdminHomepageActivity.this, CalendarActivity.class);
                    startActivity(intent);
                }

                else if (id == R.id.nav_log_out) { logOut();}



                else if (id == R.id.nav_categories) {
                    RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
                    recyclerView.setVisibility(View.VISIBLE);

                }



                else if (id == R.id.nav_view_profile) {
                    Intent intent = new Intent(AdminHomepageActivity.this, ProfileViewActivity.class);
                    startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_chat) {
            DrawerLayout chatDrawerLayout = findViewById(R.id.drawerLayout);

            // Toggle the chat drawer
            if (!chatDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                chatDrawerLayout.openDrawer(GravityCompat.END);
            } else {
                chatDrawerLayout.closeDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Log out?");
        dialog.setMessage("Are you sure you want to log out?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdminHomepageActivity.this, HomepageActivity.class);
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