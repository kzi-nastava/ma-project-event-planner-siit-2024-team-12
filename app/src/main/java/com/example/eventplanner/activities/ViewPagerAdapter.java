package com.example.eventplanner.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        // Kreirajte i vratite odgovarajući fragment za svaku poziciju
        switch (position) {
            case 0:
                //return new CategoryFragment1();
            case 1:
                //return new CategoryFragment2();
            case 2:
                //return new CategoryFragment3();
            case 3:
                //return new CategoryFragment4();
            default:
                //return new CategoryFragment1();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4; // Broj tabova
    }
}

