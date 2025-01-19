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

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CategoryAdapter;
import com.example.eventplanner.model.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoriesTableFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;

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

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Category A", "Description A", "Active"));
        categories.add(new Category("2", "Category B", "Description B", "Inactive"));
        categories.add(new Category("3", "Category C", "Description C", "Active"));

        CategoryAdapter adapter2 = new CategoryAdapter(categories);
        categoriesRecyclerView.setAdapter(adapter2);
    }
}