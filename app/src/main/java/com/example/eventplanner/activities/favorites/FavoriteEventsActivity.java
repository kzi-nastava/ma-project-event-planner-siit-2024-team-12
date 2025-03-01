package com.example.eventplanner.activities.favorites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.UserRole;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.adapters.FavoriteEventsAdapter;
import com.example.eventplanner.dto.event.AcceptedEventDTO;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteEventsAdapter adapter;
    private List<FavEventDTO> allEvents = new ArrayList<>();
    private List<FavEventDTO> currentEvents = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_events);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllEvents();

        adapter = new FavoriteEventsAdapter(currentEvents);
        recyclerView.setAdapter(adapter);

        loadPage(currentPage);

        findViewById(R.id.previousPage).setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage(currentPage);
            }
        });

        findViewById(R.id.nextPage).setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPage(currentPage);
            }
        });

        updatePageUI();
    }



    private void updatePageUI() {
        TextView pageNumberText = findViewById(R.id.pageNumber);
        pageNumberText.setText("Page " + currentPage + " / " + getTotalPages());
    }


    private void loadAllEvents() {
        String auth = ClientUtils.getAuthorization(this);
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "e");

        final List<FavEventDTO>[] favEvents = new List[]{new ArrayList<>()};

        Call<ArrayList<FavEventDTO>> call = ClientUtils.userService.getFavoriteEvents(auth, email);


        call.enqueue(new Callback<ArrayList<FavEventDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FavEventDTO>> call, Response<ArrayList<FavEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favEvents[0] = response.body();
                    allEvents.clear();
                    allEvents.addAll(favEvents[0]);
                    loadPage(currentPage);
                } else {
                    Toast.makeText(FavoriteEventsActivity.this, "Error loading favorites: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavEventDTO>> call, Throwable t) {
                Toast.makeText(FavoriteEventsActivity.this, "Failed to load favorite events!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadPage(int page) {
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allEvents.size());

        currentEvents.clear();
        currentEvents.addAll(allEvents.subList(startIndex, endIndex));
        adapter.notifyDataSetChanged();

        updatePageUI();
    }


    private int getTotalPages() {
        return (int) Math.ceil((double) allEvents.size() / PAGE_SIZE);
    }

    public void closeForm(View view) {
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = pref.getString("userRole", UserRole.ROLE_ORGANIZER.toString());

        if (role.equals(UserRole.ROLE_ORGANIZER.toString())) {
            Intent intent = new Intent(FavoriteEventsActivity.this, OrganiserHomepageActivity.class);
            startActivity(intent);
        }
        else if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            Intent intent = new Intent(FavoriteEventsActivity.this, ProviderHomepageActivity.class);
            startActivity(intent);
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}

