package com.example.eventplanner.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.auth.SignUpPagerAdapter;
import com.example.eventplanner.viewmodels.SignUpViewModel;

public class SignUpFragment extends DialogFragment {

    private ViewPager2 viewPager;
    private SignUpPagerAdapter adapter;

    private boolean isUpgrade = false;
    private String userEmail;

    private SignUpViewModel viewModel;
    private View view;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            isUpgrade = intent.getBooleanExtra("IS_UPGRADE", false);
            userEmail = intent.getStringExtra("USER_EMAIL");
        }

        viewModel.setUpgradeMode(isUpgrade);

        viewPager = view.findViewById(R.id.viewPager);
        adapter = new SignUpPagerAdapter(this, isUpgrade, userEmail);
        viewPager.setAdapter(adapter);


        return view;
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

