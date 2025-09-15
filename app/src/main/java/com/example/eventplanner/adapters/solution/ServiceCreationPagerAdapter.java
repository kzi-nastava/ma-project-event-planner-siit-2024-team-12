package com.example.eventplanner.adapters.solution;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.servicecreation.ServiceCreation;
import com.example.eventplanner.fragments.servicecreation.ServiceCreation2;
import com.example.eventplanner.fragments.servicecreation.ServiceCreation3;
import com.example.eventplanner.fragments.servicecreation.ServiceCreation4;
import com.example.eventplanner.fragments.servicecreation.ServiceCreation5;

public class ServiceCreationPagerAdapter extends FragmentStateAdapter {

    public ServiceCreationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
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
            case 4:
                return new ServiceCreation5();
            default:
                return new ServiceCreation();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // steps
    }
}
