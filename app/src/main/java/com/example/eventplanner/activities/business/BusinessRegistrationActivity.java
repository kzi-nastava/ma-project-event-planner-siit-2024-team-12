package com.example.eventplanner.activities.business;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.auth.BusinessRegPager;

public class BusinessRegistrationActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BusinessRegPager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_registration);

        viewPager = findViewById(R.id.viewPager);
        adapter = new BusinessRegPager(this);
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