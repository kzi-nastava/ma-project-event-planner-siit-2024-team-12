package com.example.eventplanner.activities.favorites;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.FavoriteEventsAdapter;
import com.example.eventplanner.dto.event.FavEventDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        pageNumberText.setText("Strana " + currentPage + " / " + getTotalPages());
    }


    private void loadAllEvents() {
        allEvents.add(new FavEventDTO(1L, "Event 1", "image_url_1", "city", "country", LocalDate.now(), LocalTime.now(), "img"));
        allEvents.add(new FavEventDTO(2L, "Event 2", "image_url_2", "city", "country", LocalDate.now(), LocalTime.now(), "img"));
        allEvents.add(new FavEventDTO(3L, "Event 3", "image_url_3", "city", "country", LocalDate.now(), LocalTime.now(), "img"));
        allEvents.add(new FavEventDTO(4L, "Event 4", "image_url_4", "city", "country", LocalDate.now(), LocalTime.now(), "img"));
        allEvents.add(new FavEventDTO(5L, "Event 5", "image_url_5", "city", "country", LocalDate.now(), LocalTime.now(), "img"));
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

}

