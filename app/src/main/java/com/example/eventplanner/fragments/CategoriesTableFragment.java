package com.example.eventplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.solution.CategoryAdapter;
import com.example.eventplanner.dto.business.GetBusinessDTO;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoriesTableFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private String companyEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories_table, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoriesRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getCurrentBusiness();
    }


    private void loadCategories() {
        String auth = ClientUtils.getAuthorization(requireContext());
        final List<GetSolutionCategoryDTO>[] categories = new List[]{new ArrayList()};

        Call<ArrayList<GetSolutionCategoryDTO>> call = ClientUtils.businessService.getSolutionCategoriesByBusiness(auth, companyEmail);

        call.enqueue(new Callback<ArrayList<GetSolutionCategoryDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GetSolutionCategoryDTO>> call, Response<ArrayList<GetSolutionCategoryDTO>> response) {
                if (response.isSuccessful()) {
                    categories[0] = response.body();

                    CategoryAdapter adapter = new CategoryAdapter(categories[0]);
                    categoriesRecyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetSolutionCategoryDTO>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load categories!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getCurrentBusiness() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(auth);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCompanyEmail() == null) {
                        Toast.makeText(getActivity(), "No event types for your business!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        companyEmail = response.body().getCompanyEmail();
                        loadCategories();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load the current business!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}