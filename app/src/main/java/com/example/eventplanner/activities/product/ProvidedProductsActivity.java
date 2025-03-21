package com.example.eventplanner.activities.product;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.favorites.FavoriteProductsActivity;
import com.example.eventplanner.adapters.favorites.FavoriteProductsAdapter;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.solution.FavSolutionDTO;
import com.example.eventplanner.dto.solution.GetProductDTO;
import com.example.eventplanner.fragments.product.ProductCreationFragment;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProvidedProductsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteProductsAdapter adapter;
    private List<FavSolutionDTO> allProducts = new ArrayList<>();
    private List<FavSolutionDTO> currentProducts = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;
    private Button addProductBtn, filterBtn;
    private ImageView exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_provided_products);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCurrentBusiness();

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

        addProductBtn = findViewById(R.id.addProductBtn);
        addProductBtn.setOnClickListener(v -> {
            ProductCreationFragment fragment = new ProductCreationFragment();
            fragment.show(getSupportFragmentManager(), "ProductCreationFragment");
        });


        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this::closeForm);
    }


    private void updatePageUI() {
        TextView pageNumberText = findViewById(R.id.pageNumber);
        pageNumberText.setText("Page " + currentPage + " / " + getTotalPages());
    }


    private void loadProvidedProducts(Long businessId) {
        String auth = ClientUtils.getAuthorization(this);

        final List<GetProductDTO>[] providedProducts = new List[]{new ArrayList<>()};

        Call<ArrayList<GetProductDTO>> call = ClientUtils.businessService.getProvidersProducts(auth, businessId);
        call.enqueue(new Callback<ArrayList<GetProductDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetProductDTO>> call, Response<ArrayList<GetProductDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    providedProducts[0] = response.body();
                    allProducts.clear();
                    allProducts.addAll(convertToFavDTO(providedProducts[0]));
                    loadPage(currentPage);
                }
                else {
                    Toast.makeText(ProvidedProductsActivity.this, "Error loading provided products!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetProductDTO>> call, Throwable t) {
                Toast.makeText(ProvidedProductsActivity.this, "Failed to load provided products!", Toast.LENGTH_SHORT).show();
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
        setResult(RESULT_CANCELED);
        finish();
    }


    private List<FavSolutionDTO> convertToFavDTO(List<GetProductDTO> providedProducts) {
        List<FavSolutionDTO> converted = new ArrayList<>();

        for (GetProductDTO getProductDTO : providedProducts) {
            converted.add(new FavSolutionDTO(getProductDTO.getId(),
                                             getProductDTO.getName(),
                                             getProductDTO.getDescription(),
                                             getProductDTO.getMainImageUrl(),
                                             getProductDTO.getCity(),
                                             getProductDTO.getPrice(),
                                             getProductDTO.getCategoryName()));
        }

        return converted;
    }


    private void loadCurrentBusiness() {
        String auth = ClientUtils.getAuthorization(this);

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(auth);
        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadProvidedProducts(response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {

            }
        });
    }
}