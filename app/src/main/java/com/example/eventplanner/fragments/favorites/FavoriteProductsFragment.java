package com.example.eventplanner.fragments.favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.fragments.product.ProductDetailsFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.favorites.FavoriteProductsAdapter;
import com.example.eventplanner.dto.solution.FavSolutionDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteProductsAdapter adapter;
    private List<FavSolutionDTO> allProducts = new ArrayList<>();
    private List<FavSolutionDTO> currentProducts = new ArrayList<>();
    private static final int PAGE_SIZE = 3;
    private int currentPage = 1;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_products, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadAllProducts();

        adapter = new FavoriteProductsAdapter(currentProducts, productId -> {
            ProductDetailsFragment detailsFragment = ProductDetailsFragment.newInstance(productId);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadPage(currentPage);

        view.findViewById(R.id.previousPage).setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage(currentPage);
            }
        });

        view.findViewById(R.id.nextPage).setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadPage(currentPage);
            }
        });

        updatePageUI();

        return view;
    }



    private void updatePageUI() {
        TextView pageNumberText = view.findViewById(R.id.pageNumber);
        pageNumberText.setText("Page " + currentPage + " / " + getTotalPages());
    }


    private void loadAllProducts() {
        String auth = ClientUtils.getAuthorization(requireContext());
        SharedPreferences pref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
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
                    Toast.makeText(requireActivity(), "Error loading favorite products!" + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FavSolutionDTO>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load favorite products!",
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

}