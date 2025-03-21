package com.example.eventplanner.adapters.solution;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.product.ProductCreationFragment;
import com.example.eventplanner.fragments.product.ProductCreationFragment2;

public class ProductCreationPager extends FragmentStateAdapter {

    public ProductCreationPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ProductCreationFragment2();
            default:
                return new ProductCreationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
