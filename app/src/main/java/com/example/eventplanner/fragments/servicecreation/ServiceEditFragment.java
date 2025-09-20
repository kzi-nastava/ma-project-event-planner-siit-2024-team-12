package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.example.eventplanner.enumeration.ReservationType;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.MultiSelectSpinner;
import com.example.eventplanner.viewmodels.ServiceEditViewModel;
import com.google.android.material.imageview.ShapeableImageView;

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

        AppCompatButton editButton = view.findViewById(R.id.saveServiceEdit);
        AppCompatButton deleteButton = view.findViewById(R.id.saveServiceDelete);
        View closeFormButton = view.findViewById(R.id.imageView5);

        editButton.setOnClickListener(v -> {
            // Ažuriranje DTO-a s odabranim Event tipovima
            List<GetEventTypeDTO> selectedEventTypes = eventTypeSpinner.getSelectedItems(allEventTypes);
            if(viewModel.getServiceData().getValue() != null) {
                viewModel.getServiceData().getValue().setEventTypes(selectedEventTypes); // Promeni setEventType u svom DTO-u ako je potrebno
            }

            // viewModel.editService(); // Odkomentiraj ovo kada budeš imao implementiranu logiku za slanje izmjena
            Toast.makeText(getContext(), R.string.service_edited, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
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

        // Kreiranje Observer-a za serviceData
        viewModel.getServiceData().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
                populateFields(service);
            }
        });
        // Kreiranje Observer-a za EventTypes
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null) {
                this.allEventTypes = eventTypes;
                // Popuni spinner nakon što se učitaju i EventTypes i ServiceData
                if (viewModel.getServiceData().getValue() != null) {
                    populateFields(viewModel.getServiceData().getValue());
                }
            }
        });

        // Učitavanje tipova događaja
        viewModel.fetchEventTypes();
    }

    /**
     * Puni View komponente sa podacima iz GetServiceDTO objekta.
     * @param service Objekat sa podacima o usluzi.
     */
    private void populateFields(GetServiceDTO service) {
        // Popunjavanje EditText polja
        serviceNameEditText.setText(service.getName());
        servicePriceEditText.setText(String.format(Locale.getDefault(), "%.2f", service.getPrice()));
        serviceDiscountEditText.setText(String.format(Locale.getDefault(), "%.0f", service.getDiscount()));
        descriptionEditText.setText(service.getDescription());
        specsEditText.setText(service.getSpecifics());

        // Popunjavanje Event Type Spinnera
        if (allEventTypes != null && service.getEventTypes() != null) {
            eventTypeSpinner.setItems(allEventTypes, service.getEventTypes());
        }

        // Popunjavanje ImageView (uz pomoć Glide biblioteke)
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(ClientUtils.BASE_IMAGE_URL + service.getImageUrl())
                    .placeholder(R.drawable.shopping_cart) // slika za učitavanje
                    .into(serviceImage);
        }

        // Popunjavanje Spinnera
        // Napomena: Za spinnere, prvo morate dobiti ArrayAdapter,
        // a zatim pronaći indeks itema i postaviti selekciju.

        // Za kategoriju, koja je EditText, postavite ime
        if (service.getCategory() != null) {
            serviceCategoryEditText.setText(service.getCategory());
        }

        // Popunjavanje i postavljanje listenera za Visibility spinner
        ArrayAdapter<CharSequence> visibilityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.service_visibility_spinner, android.R.layout.simple_spinner_item);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(visibilityAdapter);
        if (service.getVisible() != null) {
            int position = service.getVisible() ? 0 : 1;
            visibilitySpinner.setSelection(position);
        }

        // Dodavanje listenera za promenu selekcije
        visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pozicija 0 je "Visible" (true), pozicija 1 je "Hidden" (false)
                boolean isVisible = position == 0;
                // Ažuriranje atributa u ViewModelu
                viewModel.getServiceData().getValue().setVisible(isVisible);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne radite ništa
            }
        });

        // Popunjavanje i postavljanje listenera za Availability spinner
        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.service_availability_spinner, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        if (service.getAvailable() != null) {
            int position = service.getAvailable() ? 0 : 1;
            availabilitySpinner.setSelection(position);
        }

        // Dodavanje listenera za promenu selekcije
        availabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pozicija 0 je "Available" (true), pozicija 1 je "Not Available" (false)
                boolean isAvailable = position == 0;
                // Ažuriranje atributa u ViewModelu
                viewModel.getServiceData().getValue().setAvailable(isAvailable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne radite ništa
            }
        });
        // --- Postavljanje Reservation Type Spinnera ---
        ArrayAdapter<CharSequence> reservationTypeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.service_reservation_confirmation_spinner,
                android.R.layout.simple_spinner_item
        );
        reservationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reservationTypeSpinner.setAdapter(reservationTypeAdapter);

        // Postavljanje inicijalne selekcije na osnovu ucitane usluge
        if (service.getReservationType() != null) {
            int position = reservationTypeAdapter.getPosition(service.getReservationType().name());
            if (position >= 0) {
                reservationTypeSpinner.setSelection(position);
            }
        }

        // Listener koji preslikava odabrani tip u DTO objekat
        reservationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                // Ažuriranje vrednosti u ViewModelu
                viewModel.getServiceData().getValue().setReservationType(ReservationType.valueOf(selectedType));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne raditi ništa
            }
        });

        // Primer za spinner (potrebno je imati adapter i listu opcija)
        // String[] reservationOptions = getResources().getStringArray(R.array.service_reservation_confirmation_spinner);
        // int reservationIndex = Arrays.asList(reservationOptions).indexOf(service.getReservationType().toString());
        // if (reservationIndex >= 0) {
        //    reservationTypeSpinner.setSelection(reservationIndex);
        // }
    }
}