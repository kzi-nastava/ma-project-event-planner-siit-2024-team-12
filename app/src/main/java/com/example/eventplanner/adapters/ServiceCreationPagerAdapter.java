package com.example.eventplanner.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.ServiceCreation;
import com.example.eventplanner.fragments.ServiceCreation2;
import com.example.eventplanner.fragments.ServiceCreation3;
import com.example.eventplanner.fragments.ServiceCreation4;

public class ServiceCreationPagerAdapter extends FragmentStateAdapter {

    public ServiceCreationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ServiceCreation2();
            case 2:
                return new ServiceCreation3();
            case 3:
                return new ServiceCreation4();
            default:
                return new ServiceCreation();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // steps
    }
}
