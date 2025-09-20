package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.dto.solutionservice.UpdateServiceDTO;
import com.example.eventplanner.enumeration.ReservationType;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.MultiSelectSpinner;
import com.example.eventplanner.viewmodels.ServiceEditViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceEditFragment extends Fragment {

    private ServiceEditViewModel viewModel;
    private static final String ARG_SERVICE_ID = "service_id";

    private ShapeableImageView serviceImage;
    private EditText serviceNameEditText;
    private EditText servicePriceEditText;
    private EditText serviceDiscountEditText;
    private EditText serviceCategoryEditText;
    private Spinner reservationTypeSpinner;
    private Spinner visibilitySpinner;
    private Spinner availabilitySpinner;
    private EditText descriptionEditText;

    private EditText specsEditText;
    private MultiSelectSpinner eventTypeSpinner;
    private List<GetEventTypeDTO> allEventTypes;

    private RadioButton fixedTimeRadioButton;
    private RadioButton flexibleTimeRadioButton;
    private LinearLayout fixedTimeLayout;
    private LinearLayout flexibleTimeLayout;
    private EditText fixedTimeHoursEditText;
    private EditText fixedTimeMinutesEditText;
    private EditText flexibleTimeFromEditText;
    private EditText flexibleTimeToEditText;

    public ServiceEditFragment() {
        // Obavezan prazan konstruktor za fragmente
    }

    public static ServiceEditFragment newInstance(Long serviceId) {
        ServiceEditFragment fragment = new ServiceEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ServiceEditViewModel.class);
        if (getArguments() != null) {
            Long serviceId = getArguments().getLong(ARG_SERVICE_ID);
            viewModel.fetchService(serviceId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_edit, container, false);

        serviceImage = view.findViewById(R.id.shapeableImageView);
        serviceNameEditText = view.findViewById(R.id.editTextServiceName);
        servicePriceEditText = view.findViewById(R.id.editTextServicePrice);
        serviceDiscountEditText = view.findViewById(R.id.editTextServiceDiscount);
        serviceCategoryEditText = view.findViewById(R.id.editTextServiceCategory);
        eventTypeSpinner = view.findViewById(R.id.spinnerEventType);
        reservationTypeSpinner = view.findViewById(R.id.spinnerReservationType);
        visibilitySpinner = view.findViewById(R.id.spinnerVisibility);
        availabilitySpinner = view.findViewById(R.id.spinnerAvailability);
        descriptionEditText = view.findViewById(R.id.editTextDescription);
        specsEditText = view.findViewById(R.id.editTextSpecs);

        fixedTimeRadioButton = view.findViewById(R.id.radioFixedTime);
        flexibleTimeRadioButton = view.findViewById(R.id.radioFlexibleTime);
        fixedTimeLayout = view.findViewById(R.id.fixedTimeLayout);
        flexibleTimeLayout = view.findViewById(R.id.flexibleTimeLayout);
        fixedTimeHoursEditText = view.findViewById(R.id.editTextFixedTimeHours);
        fixedTimeMinutesEditText = view.findViewById(R.id.editTextFixedTimeMinutes);
        flexibleTimeFromEditText = view.findViewById(R.id.editTextFlexibleFrom);
        flexibleTimeToEditText = view.findViewById(R.id.editTextFlexibleTo);

        AppCompatButton editButton = view.findViewById(R.id.saveServiceEdit);
        AppCompatButton deleteButton = view.findViewById(R.id.saveServiceDelete);
        View closeFormButton = view.findViewById(R.id.imageView5);

        editButton.setOnClickListener(v -> {
//            List<GetEventTypeDTO> selectedEventTypes = eventTypeSpinner.getSelectedItems(allEventTypes);
//            if(viewModel.getServiceData().getValue() != null) {
//                viewModel.getServiceData().getValue().setEventTypes(selectedEventTypes); // Promeni setEventType u svom DTO-u ako je potrebno
//            }
            updateServiceDataFromUI();

            // viewModel.editService();
//            Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
//            if (getActivity() != null) {
//                getActivity().getSupportFragmentManager().popBackStack();
//            }
        });

        deleteButton.setOnClickListener(v -> {
            // viewModel.deleteService();
            Toast.makeText(getContext(), R.string.service_deleted, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        closeFormButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getServiceData().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
                populateFields(service);
            }
        });
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null) {
                this.allEventTypes = eventTypes;
                if (viewModel.getServiceData().getValue() != null) {
                    populateFields(viewModel.getServiceData().getValue());
                }
            }
        });

        RadioGroup radioGroupTimeType = view.findViewById(R.id.radioGroupTimeType);

        radioGroupTimeType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioFixedTime) {
                fixedTimeLayout.setVisibility(View.VISIBLE);
                flexibleTimeLayout.setVisibility(View.GONE);
                flexibleTimeFromEditText.setText("");
                flexibleTimeToEditText.setText("");
            } else if (checkedId == R.id.radioFlexibleTime) {
                fixedTimeLayout.setVisibility(View.GONE);
                flexibleTimeLayout.setVisibility(View.VISIBLE);
                fixedTimeHoursEditText.setText("");
                fixedTimeMinutesEditText.setText("");
            }
        });


        viewModel.fetchEventTypes();
    }
    private void populateFields(GetServiceDTO service) {
        serviceNameEditText.setText(service.getName());
        servicePriceEditText.setText(String.format(Locale.getDefault(), "%.2f", service.getPrice()));
        serviceDiscountEditText.setText(String.format(Locale.getDefault(), "%.0f", service.getDiscount()));
        descriptionEditText.setText(service.getDescription());
        specsEditText.setText(service.getSpecifics());

        if (service.getFixedTime() != null && service.getFixedTime() > 0) {
            fixedTimeRadioButton.setChecked(true);
            fixedTimeLayout.setVisibility(View.VISIBLE);
            flexibleTimeLayout.setVisibility(View.GONE);

            long totalMinutes = service.getFixedTime();
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            fixedTimeHoursEditText.setText(String.valueOf(hours));
            fixedTimeMinutesEditText.setText(String.valueOf(minutes));
        } else if (service.getMinTime() != null && service.getMaxTime() != null) {
            flexibleTimeRadioButton.setChecked(true);
            fixedTimeLayout.setVisibility(View.GONE);
            flexibleTimeLayout.setVisibility(View.VISIBLE);


            if (service.getMinTime() != null) {
                flexibleTimeFromEditText.setText(String.valueOf(service.getMinTime()));
            }
            if (service.getMaxTime() != null) {
                flexibleTimeToEditText.setText(String.valueOf(service.getMaxTime()));
            }

        } else {
            // Podrazumevano stanje (npr. fiksno vrijeme)
            fixedTimeRadioButton.setChecked(true);
            fixedTimeLayout.setVisibility(View.VISIBLE);
            flexibleTimeLayout.setVisibility(View.GONE);
        }

        if (allEventTypes != null && service.getEventTypes() != null) {
            eventTypeSpinner.setItems(allEventTypes, service.getEventTypes());
        }

        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(ClientUtils.BASE_IMAGE_URL + service.getImageUrl())
                    .placeholder(R.drawable.shopping_cart)
                    .into(serviceImage);
        }

        if (service.getCategory() != null) {
            serviceCategoryEditText.setText(service.getCategory());
        }

        ArrayAdapter<CharSequence> visibilityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.service_visibility_spinner, android.R.layout.simple_spinner_item);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(visibilityAdapter);
        if (service.getVisible() != null) {
            int position = service.getVisible() ? 0 : 1;
            visibilitySpinner.setSelection(position);
        }

        visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isVisible = position == 0;
                viewModel.getServiceData().getValue().setVisible(isVisible);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.service_availability_spinner, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        if (service.getAvailable() != null) {
            int position = service.getAvailable() ? 0 : 1;
            availabilitySpinner.setSelection(position);
        }

        availabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isAvailable = position == 0;
                viewModel.getServiceData().getValue().setAvailable(isAvailable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<CharSequence> reservationTypeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.service_reservation_confirmation_spinner,
                android.R.layout.simple_spinner_item
        );
        reservationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reservationTypeSpinner.setAdapter(reservationTypeAdapter);

        if (service.getReservationType() != null) {
            int position = reservationTypeAdapter.getPosition(service.getReservationType().name());
            if (position >= 0) {
                reservationTypeSpinner.setSelection(position);
            }
        }

        reservationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                viewModel.getServiceData().getValue().setReservationType(ReservationType.valueOf(selectedType));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateServiceDataFromUI() {
        UpdateServiceDTO updateServiceDTO = new UpdateServiceDTO();

        updateServiceDTO.setName(serviceNameEditText.getText().toString());
        updateServiceDTO.setDescription(descriptionEditText.getText().toString());
        updateServiceDTO.setSpecifics(specsEditText.getText().toString());

        try {
            if (!servicePriceEditText.getText().toString().isEmpty()) {
                updateServiceDTO.setPrice(Double.parseDouble(servicePriceEditText.getText().toString()));
            }
            if (!serviceDiscountEditText.getText().toString().isEmpty()) {
                updateServiceDTO.setDiscount(Double.parseDouble(serviceDiscountEditText.getText().toString()));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number for price and discount.", Toast.LENGTH_SHORT).show();
            return;
        }

        updateServiceDTO.setVisible(visibilitySpinner.getSelectedItemPosition() == 0);
        updateServiceDTO.setAvailable(availabilitySpinner.getSelectedItemPosition() == 0);
        String selectedReservationType = (String) reservationTypeSpinner.getSelectedItem();
        updateServiceDTO.setReservationType(ReservationType.valueOf(selectedReservationType));

        List<GetEventTypeDTO> selectedEventTypes = eventTypeSpinner.getSelectedItems(allEventTypes);
        List<Long> eventTypeIds = new ArrayList<>();
        for (GetEventTypeDTO eventType : selectedEventTypes) {
            eventTypeIds.add(eventType.getId());
        }
        updateServiceDTO.setEventTypeIds(eventTypeIds);

        if (fixedTimeRadioButton.isChecked()) {
            long hours = 0;
            long minutes = 0;
            if (!fixedTimeHoursEditText.getText().toString().isEmpty()) {
                hours = Long.parseLong(fixedTimeHoursEditText.getText().toString());
            }
            if (!fixedTimeMinutesEditText.getText().toString().isEmpty()) {
                minutes = Long.parseLong(fixedTimeMinutesEditText.getText().toString());
            }
            long totalMinutes = hours * 60 + minutes;

            updateServiceDTO.setFixedTime(Duration.ofMinutes(totalMinutes));
            updateServiceDTO.setMinTime(Duration.ofHours(0));
            updateServiceDTO.setMaxTime(Duration.ofHours(0));
        } else if (flexibleTimeRadioButton.isChecked()) {
            long minHours = 0;
            long maxHours = 0;
            if (!flexibleTimeFromEditText.getText().toString().isEmpty()) {
                minHours = Long.parseLong(flexibleTimeFromEditText.getText().toString());
            }
            if (!flexibleTimeToEditText.getText().toString().isEmpty()) {
                maxHours = Long.parseLong(flexibleTimeToEditText.getText().toString());
            }

            updateServiceDTO.setMinTime(Duration.ofHours(minHours));
            updateServiceDTO.setMaxTime(Duration.ofHours(maxHours));
            updateServiceDTO.setFixedTime(Duration.ofMinutes(0));
        }

        GetServiceDTO currentService = viewModel.getServiceData().getValue();
        if (currentService != null) {
            updateServiceDTO.setImageUrl(currentService.getImageUrl());
            updateServiceDTO.setCategory(currentService.getCategory());
        }

        // viewModel.editService(updateServiceDTO);
    }
}