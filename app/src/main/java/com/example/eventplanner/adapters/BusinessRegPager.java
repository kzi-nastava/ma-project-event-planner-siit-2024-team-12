package com.example.eventplanner.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.BusinessRegistration1;
import com.example.eventplanner.fragments.BusinessRegistration2;
import com.example.eventplanner.fragments.SignUp1;
import com.example.eventplanner.fragments.SignUp2;
import com.example.eventplanner.fragments.SignUp3;


public class BusinessRegPager extends FragmentStateAdapter {

    public BusinessRegPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BusinessRegistration2();
            default:
                return new BusinessRegistration1();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // steps
    }
}
