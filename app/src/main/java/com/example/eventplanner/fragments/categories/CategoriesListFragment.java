package com.example.eventplanner.fragments.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.categories.CategoryAdapter;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.viewmodels.CategoryViewModel;

public class CategoriesListFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener{

    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;
    private String categoryType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryType = getArguments() != null ? getArguments().getString("category_type") : "active";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        adapter = new CategoryAdapter("active".equals(categoryType), this);
        recyclerView.setAdapter(adapter);


        if ("active".equals(categoryType)) {
            viewModel.getActiveCategories().observe(getViewLifecycleOwner(), adapter::setCategories);
        } else if ("recommended".equals(categoryType)) {
            viewModel.getRecommendedCategories().observe(getViewLifecycleOwner(), adapter::setCategories);
//            viewModel.fetchRecommendedCategories();
        }

        return view;
    }
    @Override
    public void onCategoryClick(GetSolutionCategoryDTO category, boolean isActive) {
        // Kreiraj i prikaži novi DialogFragment
        CategoryDetailsFragment detailsFragment = CategoryDetailsFragment.newInstance(category, isActive);

        // show() metoda prikaže fragment kao dijalog
        detailsFragment.show(getParentFragmentManager(), "category_details_dialog");
    }
    public void refreshData() {
        if ("active".equals(categoryType)) {
            viewModel.fetchActiveCategories();
        } else if ("recommended".equals(categoryType)) {
            viewModel.fetchRecommendedCategories();
        }
    }
}