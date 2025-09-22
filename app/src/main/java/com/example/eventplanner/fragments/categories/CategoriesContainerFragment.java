package com.example.eventplanner.fragments.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.categories.CategoriesPagerAdapter;
import com.example.eventplanner.viewmodels.CategoryViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategoriesContainerFragment extends Fragment implements CategoryCreationFragment.OnCategoryCreationListener {
    private CategoryViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_container, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutCategories);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerCategories);
        ImageButton createCategoryButton = view.findViewById(R.id.buttonCreateNewCategory);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        CategoriesPagerAdapter adapter = new CategoriesPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Active");
            } else {
                tab.setText("Recommended");
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    viewModel.fetchActiveCategories();
                } else {
                    viewModel.fetchRecommendedCategories();
                }
            }
        });

        viewModel.fetchActiveCategories();

        createCategoryButton.setOnClickListener(v -> {
            // Kreiraj instancu novog fragmenta, ali sad je to DialogFragment
            CategoryCreationFragment newFragment = CategoryCreationFragment.newInstance("CREATE");

            // Prikazi dijalog
            newFragment.show(getChildFragmentManager(), "category_creation_dialog");
        });


        return view;
    }
    @Override
    public void onCategoryCreated() {
        // Logika za kada je kategorija uspešno kreirana
        Toast.makeText(getContext(), "Kategorija kreirana!", Toast.LENGTH_SHORT).show();
        // Po potrebi, osveži podatke
        viewModel.fetchActiveCategories();
    }

    @Override
    public void onCategorySuggested() {
        // Logika za kada je kategorija uspešno predložena
        Toast.makeText(getContext(), "Kategorija predložena!", Toast.LENGTH_SHORT).show();
        viewModel.fetchRecommendedCategories();
    }

    @Override
    public void onCategoryCreationCanceled() {
        // Logika za kada je korisnik odustao
        Toast.makeText(getContext(), "Kreiranje otkazano.", Toast.LENGTH_SHORT).show();
    }
}