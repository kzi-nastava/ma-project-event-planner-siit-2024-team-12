package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.eventplanner.utils.ImageHelper;
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
    private EditText serviceResDeadlineEditText;
    private EditText serviceCancDeadlineEditText;
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
    private ImageButton changeImageButton;
    private Uri newImageUri;

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
        serviceResDeadlineEditText = view.findViewById(R.id.editTextResDeadline);
        serviceCancDeadlineEditText = view.findViewById(R.id.editTextCancelDeadline);
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

        changeImageButton = view.findViewById(R.id.changeImageButton);

        AppCompatButton editButton = view.findViewById(R.id.saveServiceEdit);
        AppCompatButton deleteButton = view.findViewById(R.id.saveServiceDelete);
        View closeFormButton = view.findViewById(R.id.imageView5);

        editButton.setOnClickListener(v -> {
            updateServiceDataFromUI();
        });
        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        deleteButton.setOnClickListener(v -> {
            viewModel.deleteService(viewModel.getServiceData().getValue().getId(), () -> {
                        Toast.makeText(getContext(), R.string.service_deleted, Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    },
                    () -> {
                        Toast.makeText(getContext(), "Failed to delete service.", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
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

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (viewModel != null) {
                        viewModel.setNewImageUri(selectedImageUri);
                    }
                    serviceImage.setImageURI(selectedImageUri);
                }
            });
    private void populateFields(GetServiceDTO service) {
        serviceNameEditText.setText(service.getName());
        servicePriceEditText.setText(String.format(Locale.getDefault(), "%.0f", service.getPrice()));
        serviceDiscountEditText.setText(String.format(Locale.getDefault(), "%.0f", service.getDiscount()));
        serviceResDeadlineEditText.setText(String.valueOf(service.getReservationDeadline()));
        serviceCancDeadlineEditText.setText(String.valueOf(service.getCancellationDeadline()));
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
        if (serviceNameEditText.getText().toString().trim().isEmpty() ||
                servicePriceEditText.getText().toString().trim().isEmpty() ||
                serviceResDeadlineEditText.getText().toString().trim().isEmpty() ||
                serviceCancDeadlineEditText.getText().toString().trim().isEmpty() ||
                descriptionEditText.getText().toString().trim().isEmpty() ||
                specsEditText.getText().toString().trim().isEmpty() ||
                serviceDiscountEditText.getText().toString().trim().isEmpty()
        ) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eventTypeSpinner.getSelectedItems(allEventTypes).isEmpty()) {
            Toast.makeText(getContext(), "Please choose at least one event type.", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateServiceDTO updateServiceDTO = new UpdateServiceDTO();

        try {
            double price = Double.parseDouble(servicePriceEditText.getText().toString());
            double discount = 0;
            if (!serviceDiscountEditText.getText().toString().isEmpty()) {
                discount = Double.parseDouble(serviceDiscountEditText.getText().toString());
            }
            int resDeadline = Integer.parseInt(serviceResDeadlineEditText.getText().toString());
            int cancDeadline = Integer.parseInt(serviceCancDeadlineEditText.getText().toString());

            if (discount < 0 || discount > 99) {
                Toast.makeText(getContext(), "Discount must be between 0 and 99.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resDeadline <= cancDeadline) {
                Toast.makeText(getContext(), "Reservation deadline must be greater than cancellation deadline.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateServiceDTO.setPrice(price);
            updateServiceDTO.setDiscount(discount);
            updateServiceDTO.setReservationDeadline(resDeadline);
            updateServiceDTO.setCancellationDeadline(cancDeadline);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers for price, discount, and deadlines.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fixedTimeRadioButton.isChecked()) {
            if(fixedTimeHoursEditText.getText().toString().isEmpty() || fixedTimeMinutesEditText.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Please fill in hours and minutes.", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                long hours = 0;
                long minutes = 0;
                if (!fixedTimeHoursEditText.getText().toString().isEmpty()) {
                    hours = Long.parseLong(fixedTimeHoursEditText.getText().toString());
                }
                if (!fixedTimeMinutesEditText.getText().toString().isEmpty()) {
                    minutes = Long.parseLong(fixedTimeMinutesEditText.getText().toString());
                }
                long totalMinutes = hours * 60 + minutes;

                if (totalMinutes <= 0) {
                    Toast.makeText(getContext(), "Fixed time must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateServiceDTO.setFixedTime(Duration.ofMinutes(totalMinutes));
                updateServiceDTO.setMinTime(Duration.ofHours(0));
                updateServiceDTO.setMaxTime(Duration.ofHours(0));
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for fixed time.", Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (flexibleTimeRadioButton.isChecked()) {
            if(flexibleTimeFromEditText.getText().toString().isEmpty() || flexibleTimeToEditText.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Please fill in minimum and maximum time.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                long minHours = 0;
                long maxHours = 0;
                if (!flexibleTimeFromEditText.getText().toString().isEmpty()) {
                    minHours = Long.parseLong(flexibleTimeFromEditText.getText().toString());
                }
                if (!flexibleTimeToEditText.getText().toString().isEmpty()) {
                    maxHours = Long.parseLong(flexibleTimeToEditText.getText().toString());
                }

                if (minHours <= 0 || maxHours <= 0) {
                    Toast.makeText(getContext(), "Min and max time must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (minHours >= maxHours) {
                    Toast.makeText(getContext(), "Min time must be less than max time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateServiceDTO.setMinTime(Duration.ofHours(minHours));
                updateServiceDTO.setMaxTime(Duration.ofHours(maxHours));
                updateServiceDTO.setFixedTime(Duration.ofMinutes(0));

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for flexible time.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        updateServiceDTO.setName(serviceNameEditText.getText().toString());
        updateServiceDTO.setDescription(descriptionEditText.getText().toString());
        updateServiceDTO.setSpecifics(specsEditText.getText().toString());

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

        GetServiceDTO currentService = viewModel.getServiceData().getValue();
        if (currentService != null) {
            updateServiceDTO.setImageUrl(currentService.getImageUrl());
            updateServiceDTO.setCategory(currentService.getCategory());
        }

         viewModel.updateService(currentService.getId(), updateServiceDTO, () -> {
//                     Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
                     if(viewModel.getNewImageUri()!=null &&  currentService.getImageUrl() != null && !currentService.getImageUrl().trim().isEmpty()){
                         viewModel.deleteImage(currentService.getId(), currentService.getImageUrl(), () -> {

                                     ImageHelper.uploadMultipleImages(getContext(), List.of(viewModel.getNewImageUri()), "service",
                                             currentService.getId(), "true",
                                             () -> {
                                                 Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
                                             },
                                             () -> {
                                                 Toast.makeText(getContext(), "Failed to update service image.", Toast.LENGTH_SHORT).show();
                                             });
//                        Toast.makeText(getContext(), "Service image edited successfully!", Toast.LENGTH_SHORT).show();
//                        if (getActivity() != null) {
//                            getActivity().getSupportFragmentManager().popBackStack();
//                        }
                                 },
                                 () -> {
                                     Toast.makeText(getContext(), "Failed to update service image.", Toast.LENGTH_SHORT).show();
//                        if (getActivity() != null) {
//                            getActivity().getSupportFragmentManager().popBackStack();
//                        }
                                 });
                     }else if(viewModel.getNewImageUri()!=null && (currentService.getImageUrl() == null ||  currentService.getImageUrl().trim().isEmpty())){
                         ImageHelper.uploadMultipleImages(getContext(), List.of(viewModel.getNewImageUri()), "service",
                                 currentService.getId(), "true",
                                 () -> {
                                     Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
                                 },
                                 () -> {
                                     Toast.makeText(getContext(), "Failed to update service image.", Toast.LENGTH_SHORT).show();
                                 });

                     }else{
                         Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
                     }
//                     if (getActivity() != null) {
//                         getActivity().getSupportFragmentManager().popBackStack();
//                     }
                 },
                 () -> {
                     Toast.makeText(getContext(), "Failed to update service.", Toast.LENGTH_SHORT).show();
//                     if (getActivity() != null) {
//                         getActivity().getSupportFragmentManager().popBackStack();
//                     }
                 });


    }
}