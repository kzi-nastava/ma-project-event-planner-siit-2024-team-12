package com.example.eventplanner.activities.favorites;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.favorites.FavoriteEventsAdapter;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorePageActivity extends AppCompatActivity {
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

        TextView title = findViewById(R.id.title);
        String explore = getString(R.string.explore_public_events);
        title.setText(explore);

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

        final List<FavEventDTO>[] openEvents = new List[]{new ArrayList<>()};

        Call<ArrayList<FavEventDTO>> call = ClientUtils.userService.getOpenEvents(auth);

        call.enqueue(new Callback<ArrayList<FavEventDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FavEventDTO>> call, Response<ArrayList<FavEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    openEvents[0] = response.body();
                    allEvents.clear();
                    allEvents.addAll(openEvents[0]);
                    loadPage(currentPage);
                } else {
                    Toast.makeText(ExplorePageActivity.this, "Error loading favorites: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavEventDTO>> call, Throwable t) {
                Toast.makeText(ExplorePageActivity.this, "Failed to load favorite events!",
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
        setResult(RESULT_CANCELED);
        finish();
    }
}
