package com.example.eventplanner.activities.favorites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.UserRole;
import com.example.eventplanner.activities.homepage.OrganiserHomepageActivity;
import com.example.eventplanner.activities.homepage.ProviderHomepageActivity;
import com.example.eventplanner.activities.product.ProvidedProductsActivity;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.favorites.FavoriteProductsAdapter;
import com.example.eventplanner.dto.solution.FavSolutionDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteProductsAdapter adapter;
    private List<FavSolutionDTO> allProducts = new ArrayList<>();
    private List<FavSolutionDTO> currentProducts = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_products);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllProducts();

        adapter = new FavoriteProductsAdapter(currentProducts);
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


    private void loadAllProducts() {
        String auth = ClientUtils.getAuthorization(this);
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = pref.getString("email", "e");

        final List<FavSolutionDTO>[] favProducts = new List[]{new ArrayList<>()};

        Call<ArrayList<FavSolutionDTO>> call = ClientUtils.userService.getFavoriteProducts(auth, email);


        call.enqueue(new Callback<ArrayList<FavSolutionDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FavSolutionDTO>> call, Response<ArrayList<FavSolutionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favProducts[0] = response.body();
                    allProducts.clear();
                    allProducts.addAll(favProducts[0]);
                    loadPage(currentPage);
                } else {
                    Toast.makeText(FavoriteProductsActivity.this, "Error loading favorite products!" + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavSolutionDTO>> call, Throwable t) {
                Toast.makeText(FavoriteProductsActivity.this, "Failed to load favorite products!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadPage(int page) {
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allProducts.size());

        currentProducts.clear();
        currentProducts.addAll(allProducts.subList(startIndex, endIndex));
        adapter.notifyDataSetChanged();

        updatePageUI();
    }


    private int getTotalPages() {
        return (int) Math.ceil((double) allProducts.size() / PAGE_SIZE);
    }


    public void closeForm(View view) {
        SharedPreferences pref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = pref.getString("userRole", UserRole.ROLE_ORGANIZER.toString());

        if (role.equals(UserRole.ROLE_ORGANIZER.toString())) {
            Intent intent = new Intent(FavoriteProductsActivity.this, OrganiserHomepageActivity.class);
            startActivity(intent);
        }
        else if (role.equals(UserRole.ROLE_PROVIDER.toString())) {
            Intent intent = new Intent(FavoriteProductsActivity.this, ProviderHomepageActivity.class);
            startActivity(intent);
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}