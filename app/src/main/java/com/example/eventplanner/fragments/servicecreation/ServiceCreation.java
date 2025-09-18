package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.solutioncategory.CategoryCreationActivity;
import com.example.eventplanner.dto.solutioncategory.GetSolutionCategoryDTO;
import com.example.eventplanner.viewmodels.ServiceCreationViewModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation extends Fragment {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private ServiceCreationViewModel viewModel;
    private Spinner categorySpinner;
    private List<GetSolutionCategoryDTO> categoryList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation newInstance(String param1, String param2) {
        ServiceCreation fragment = new ServiceCreation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(ServiceCreationViewModel.class);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Obradi rezultat ovde ako je potrebno
                        Intent data = result.getData();
                        // Na primer, možeš pročitati podatke iz Intent-a.java ovde
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_creation, container, false);
        Button nextButton = view.findViewById(R.id.nextServiceCreate);
        ImageButton newCategoryButton = view.findViewById(R.id.dugme);
        ImageView xButton = view.findViewById(R.id.imageView5);

        RadioGroup durationRadioGroup = view.findViewById(R.id.duration_radio_group);
        LinearLayout fixedDurationLayout = view.findViewById(R.id.fixed_duration_layout);
        LinearLayout flexibleDurationLayout = view.findViewById(R.id.flexible_duration_layout);

        categorySpinner = view.findViewById(R.id.spinnerServiceCreate);

        viewModel.getServiceCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                this.categoryList = categories;
                List<String> categoryNames = categories.stream()
                        .map(GetSolutionCategoryDTO::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (categoryList != null && position >= 0 && position < categoryList.size()) {
                    GetSolutionCategoryDTO selectedCategory = categoryList.get(position);

                    try {
                        String selectedCategoryId = selectedCategory.getId();

                        Long categoryIdAsLong = Long.parseLong(selectedCategoryId);

                        viewModel.addData("categoryId", categoryIdAsLong);

                    } catch (NumberFormatException e) {
                        Log.e("ViewModel", "Failed to parse categoryId to Long", e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.addData("categoryId", 1);
            }
        });

        viewModel.fetchAcceptedCategories();

        nextButton.setOnClickListener(v -> {
            if(validateForm()){
                Fragment parentFragment=getParentFragment();
                if (parentFragment instanceof ServiceCreationContainer) {
                    ((ServiceCreationContainer) parentFragment).nextPage();
                }
            }
        });

        newCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryCreationActivity.class);
            activityResultLauncher.launch(intent);
        });

        xButton.setOnClickListener(v ->{
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        durationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixed_duration_radio) {
                fixedDurationLayout.setVisibility(View.VISIBLE);
                flexibleDurationLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.flexible_duration_radio) {
                fixedDurationLayout.setVisibility(View.GONE);
                flexibleDurationLayout.setVisibility(View.VISIBLE);
            }
        });




        return view;
    }

    public boolean validateForm() {
        EditText serviceNameEditText = requireView().findViewById(R.id.editTextServiceName);
        if (serviceNameEditText.getText().toString().trim().isEmpty()) {
            serviceNameEditText.setError("Naziv usluge ne može biti prazan.");
            return false;
        }
        viewModel.addData("name", serviceNameEditText.getText().toString());

        RadioGroup durationRadioGroup = requireView().findViewById(R.id.duration_radio_group);
        int checkedRadioButtonId = durationRadioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId == R.id.fixed_duration_radio) {
            EditText fixedHoursEditText = requireView().findViewById(R.id.fixed_hours_edittext);
            EditText fixedMinutesEditText = requireView().findViewById(R.id.fixed_minutes_edittext);

            String hoursString = fixedHoursEditText.getText().toString().trim();
            String minutesString = fixedMinutesEditText.getText().toString().trim();

            if (hoursString.isEmpty()) {
                fixedHoursEditText.setError("Unesite sate.");
                return false;
            }
            if (minutesString.isEmpty()) {
                fixedMinutesEditText.setError("Unesite minute.");
                return false;
            }

            try {
                int hours = Integer.parseInt(hoursString);
                int minutes = Integer.parseInt(minutesString);

                if (hours < 0) {
                    fixedHoursEditText.setError("Sati ne mogu biti manji od 0.");
                    return false;
                }
                if (minutes < 0 || minutes > 59) {
                    fixedMinutesEditText.setError("Minuti moraju biti između 0 i 59.");
                    return false;
                }
                viewModel.addData("fixedTime", hours*60+minutes);
                viewModel.addData("minTime", 0);
                viewModel.addData("maxTime", 0);
            } catch (NumberFormatException e) {
                fixedHoursEditText.setError("Unesite ispravan broj.");
                return false;
            }

        } else if (checkedRadioButtonId == R.id.flexible_duration_radio) {
            EditText flexibleFromEditText = requireView().findViewById(R.id.flexible_from_edittext);
            EditText flexibleToEditText = requireView().findViewById(R.id.flexible_to_edittext);

            String fromString = flexibleFromEditText.getText().toString().trim();
            String toString = flexibleToEditText.getText().toString().trim();

            if (fromString.isEmpty()) {
                flexibleFromEditText.setError("Unesite početnu vrednost.");
                return false;
            }
            if (toString.isEmpty()) {
                flexibleToEditText.setError("Unesite krajnju vrednost.");
                return false;
            }

            try {
                int fromValue = Integer.parseInt(fromString);
                int toValue = Integer.parseInt(toString);

                if (fromValue < 0) {
                    flexibleFromEditText.setError("Vrednost ne može biti manja od 0.");
                    return false;
                }
                if (toValue < 0) {
                    flexibleToEditText.setError("Vrednost ne može biti manja od 0.");
                    return false;
                }
                if (fromValue >= toValue) {
                    flexibleToEditText.setError("Krajnja vrednost mora biti veća od početne.");
                    return false;
                }
                viewModel.addData("minTime", fromValue);
                viewModel.addData("maxTime", toValue);
                viewModel.addData("fixedTime", 0);
            } catch (NumberFormatException e) {
                flexibleFromEditText.setError("Unesite ispravan broj.");
                return false;
            }
        }

        return true;
    }
}