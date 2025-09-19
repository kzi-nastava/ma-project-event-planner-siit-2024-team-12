package com.example.eventplanner.adapters.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventplanner.fragments.signup.SignUp1;
import com.example.eventplanner.fragments.signup.SignUp2;
import com.example.eventplanner.fragments.signup.SignUp3;


public class SignUpPagerAdapter extends FragmentStateAdapter {

    private final boolean isUpgrade;
    private final String userEmail;

    public SignUpPagerAdapter(@NonNull FragmentActivity fragmentActivity, boolean isUpgrade, String userEmail) {
        super(fragmentActivity);
        this.isUpgrade = isUpgrade;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return SignUp2.newInstance(isUpgrade, userEmail);
            case 2:
                return SignUp3.newInstance(isUpgrade, userEmail);
            default:
                return SignUp1.newInstance(isUpgrade, userEmail);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // steps
    }
}
