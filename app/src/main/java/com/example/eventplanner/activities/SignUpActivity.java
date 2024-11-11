package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.SignUpPagerAdapter;

public class SignUpActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SignUpPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewPager = findViewById(R.id.viewPager);
        adapter = new SignUpPagerAdapter(this);
        viewPager.setAdapter(adapter);


    }

    public void nextPage() {
        if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void previousPage() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}

