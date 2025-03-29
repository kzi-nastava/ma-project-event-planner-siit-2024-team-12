package com.example.eventplanner.activities.service;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.solution.ServiceCreationPagerAdapter;

public class ServiceCreationActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ServiceCreationPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_creation);

        viewPager = findViewById(R.id.serviceCreationPager);
        adapter = new ServiceCreationPagerAdapter(this);
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

    public void closeForm(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}

