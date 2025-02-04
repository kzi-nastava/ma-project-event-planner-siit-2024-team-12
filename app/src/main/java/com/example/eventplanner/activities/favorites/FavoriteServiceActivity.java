package com.example.eventplanner.activities.favorites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.FavoriteServiceAdapter;
import com.example.eventplanner.dto.event.FavEventDTO;
import com.example.eventplanner.dto.solution.FavSolutionDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteServiceAdapter adapter;
    private List<FavSolutionDTO> allServices = new ArrayList<>();
    private List<FavSolutionDTO> currentServices = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_service);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllServices();

        adapter = new FavoriteServiceAdapter(currentServices);
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


    private void loadAllServices() {
        String auth = ClientUtils.getAuthorization(this);
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "e");

        final List<FavSolutionDTO>[] favServices = new List[]{new ArrayList<>()};

        Call<ArrayList<FavSolutionDTO>> call = ClientUtils.userService.getFavoriteServices(auth, email);


        call.enqueue(new Callback<ArrayList<FavSolutionDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FavSolutionDTO>> call, Response<ArrayList<FavSolutionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favServices[0] = response.body();
                    allServices.clear();
                    allServices.addAll(favServices[0]);
                    loadPage(currentPage);
                } else {
                    Toast.makeText(FavoriteServiceActivity.this, "Error loading favorite services!" + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavSolutionDTO>> call, Throwable t) {
                Toast.makeText(FavoriteServiceActivity.this, "Failed to load favorite services!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadPage(int page) {
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allServices.size());

        currentServices.clear();
        currentServices.addAll(allServices.subList(startIndex, endIndex));
        adapter.notifyDataSetChanged();

        updatePageUI();
    }


    private int getTotalPages() {
        return (int) Math.ceil((double) allServices.size() / PAGE_SIZE);
    }

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}