package com.example.eventplanner.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.auth.SignUpPagerAdapter;
import com.example.eventplanner.viewmodels.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SignUpPagerAdapter adapter;

    private boolean isUpgrade = false;
    private String userEmail;

    private SignUpViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            isUpgrade = intent.getBooleanExtra("IS_UPGRADE", false);
            userEmail = intent.getStringExtra("USER_EMAIL");
        }

        viewModel.setUpgradeMode(isUpgrade);

        viewPager = findViewById(R.id.viewPager);
        adapter = new SignUpPagerAdapter(this, isUpgrade, userEmail);
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

