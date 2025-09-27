package com.example.eventplanner.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.R;

public class HomepageFragment extends Fragment {

    public HomepageFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        loadChildFragments();

        return view;
    }

    private void loadChildFragments() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = childFragmentManager.beginTransaction();

        transaction.replace(R.id.cards_fragment_container, new TopEventsFragment());
        transaction.replace(R.id.cards_products_fragment_container, new TopSolutionsFragment());

        transaction.replace(R.id.events_list_fragment_container, new EventListFragment());
        transaction.replace(R.id.ps_list_fragment_container, new SolutionListFragment());

        transaction.commitAllowingStateLoss();
    }
}