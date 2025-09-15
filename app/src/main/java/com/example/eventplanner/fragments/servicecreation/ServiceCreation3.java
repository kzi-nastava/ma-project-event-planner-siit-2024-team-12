package com.example.eventplanner.fragments.servicecreation;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.enumeration.ReservationType;
import com.example.eventplanner.viewmodels.ServiceCreationViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ServiceCreationViewModel viewModel;
//    private Spinner eventTypeSpinner;

    private TextView eventTypeTextView;
    private List<GetEventTypeDTO> allEventTypes;
    private boolean[] checkedEventTypes;
    private ArrayList<Integer> selectedEventItems = new ArrayList<>();

    private Spinner visibilitySpinner;
    private Spinner reservationSpinner;
    private Spinner availabilitySpinner;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation3.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation3 newInstance(String param1, String param2) {
        ServiceCreation3 fragment = new ServiceCreation3();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_creation3, container, false);
        Button nextButton = view.findViewById(R.id.nextServiceCreate3);
        Button backButton = view.findViewById(R.id.backServiceCreate3);
        ImageView xButton = view.findViewById(R.id.imageView5);

        visibilitySpinner = view.findViewById(R.id.spinnerServiceCreate3);
        reservationSpinner = view.findViewById(R.id.spinnerServiceCreate4);
        availabilitySpinner = view.findViewById(R.id.spinnerServiceCreate2);
        eventTypeTextView = view.findViewById(R.id.eventTypeTextView); // Novi TextView u XML-u

        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null) {
                this.allEventTypes = eventTypes;
                this.checkedEventTypes = new boolean[eventTypes.size()];
            }
        });
        viewModel.fetchEventTypes();

        eventTypeTextView.setOnClickListener(v -> showMultiSelectDialog());


        List<ReservationType> reservationTypes = Arrays.asList(ReservationType.values());
        ArrayAdapter<ReservationType> reservationAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, reservationTypes);
        reservationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reservationSpinner.setAdapter(reservationAdapter);
        reservationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReservationType selectedType = (ReservationType) parent.getItemAtPosition(position);
                viewModel.addData("reservationType", selectedType.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.addData("reservationType", null);
            }
        });

        visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                boolean isVisible = selectedItem.equals("Visible");
                viewModel.addData("isVisible", isVisible);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.addData("isVisible", false);
            }
        });

        availabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                boolean isAvailable = selectedItem.equals("Available");
                viewModel.addData("isAvailable", isAvailable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.addData("isAvailable", false);
            }
        });

        nextButton.setOnClickListener(v -> {
            if(validateForm()){
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof ServiceCreationContainer) {
                    ((ServiceCreationContainer) parentFragment).nextPage();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof ServiceCreationContainer) {
                ((ServiceCreationContainer) parentFragment).previousPage();
            }
        });

        xButton.setOnClickListener(v ->{
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
    private void showMultiSelectDialog() {
        if (allEventTypes == null || allEventTypes.isEmpty()) {
            Toast.makeText(requireContext(), "Nema dostupnih tipova događaja.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] typeNames = new String[allEventTypes.size()];
        for (int i = 0; i < allEventTypes.size(); i++) {
            typeNames[i] = allEventTypes.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Odaberite tipove događaja");
        builder.setMultiChoiceItems(typeNames, checkedEventTypes, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedEventItems.add(which);
            } else {
                selectedEventItems.remove(Integer.valueOf(which));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            ArrayList<Long> selectedIds = new ArrayList<>();
            StringBuilder builderText = new StringBuilder();

            for (int i = 0; i < selectedEventItems.size(); i++) {
                int index = selectedEventItems.get(i);
                GetEventTypeDTO selectedType = allEventTypes.get(index);
                selectedIds.add(selectedType.getId());

                builderText.append(selectedType.getName());
                if (i < selectedEventItems.size() - 1) {
                    builderText.append(", ");
                }
            }

            viewModel.setSelectedEventTypeIds(selectedIds);

            eventTypeTextView.setText(builderText.toString());
        });

        builder.setNegativeButton("Poništi", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public boolean validateForm() {
        if (reservationSpinner.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Molimo odaberite tip rezervacije.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (visibilitySpinner.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Molimo odaberite vidljivost.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (availabilitySpinner.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Molimo odaberite dostupnost.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (viewModel.getSelectedEventTypeIds().getValue() == null || viewModel.getSelectedEventTypeIds().getValue().isEmpty()) {
            Toast.makeText(requireContext(), "Molimo odaberite barem jedan tip događaja.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}