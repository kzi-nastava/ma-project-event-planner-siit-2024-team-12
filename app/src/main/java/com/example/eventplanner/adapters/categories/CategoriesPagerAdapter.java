package com.example.eventplanner.adapters.categories;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.eventplanner.fragments.categories.CategoriesListFragment;

public class CategoriesPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 2;

    public CategoriesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        if (position == 0) {
            args.putString("category_type", "active");
        } else {
            args.putString("category_type", "recommended");
        }

        CategoriesListFragment fragment = new CategoriesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}