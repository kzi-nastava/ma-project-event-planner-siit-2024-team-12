package com.example.eventplanner.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.eventcreation.EventCreation1;
import com.example.eventplanner.fragments.eventcreation.EventCreation2;

public class EventCreationPager extends FragmentStateAdapter {

    public EventCreationPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EventCreation2();
            default:
                return new EventCreation1();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
