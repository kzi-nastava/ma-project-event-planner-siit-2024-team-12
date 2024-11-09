package com.example.eventplanner.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.activities.fragments.SignUp1;
import com.example.eventplanner.activities.fragments.SignUp2;
import com.example.eventplanner.activities.fragments.SignUp3;


public class SignUpPagerAdapter extends FragmentStateAdapter {

    public SignUpPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SignUp2();
            case 2:
                return new SignUp3();
            default:
                return new SignUp1();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // steps
    }
}
