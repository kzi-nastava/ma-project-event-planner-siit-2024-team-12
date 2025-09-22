package com.example.eventplanner.fragments.budgetplanning;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.budget.BudgetItemAdapter;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.viewmodels.BudgetPlanningViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Budget extends Fragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_EVENT_ID = "eventId";

    private String type;
    private Long eventId;

    private BudgetPlanningViewModel viewModel;
    private Spinner eventTypeSpinner;
    private Button showSuggestedCategoriesButton;
    private Button actionButton;
    private TextView titleTextView;
    private RecyclerView budgetItemsRecyclerView;
    private FloatingActionButton addItemFab;
    private BudgetItemAdapter budgetItemAdapter;

    public Budget() {
        // Obavezni prazan konstruktor
    }

    public static Budget newInstance(String type, @Nullable Long eventId) {
        Budget fragment = new Budget();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        if (eventId != null) {
            args.putLong(ARG_EVENT_ID, eventId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            if (getArguments().containsKey(ARG_EVENT_ID)) {
                eventId = getArguments().getLong(ARG_EVENT_ID);
            }
        }
        viewModel = new ViewModelProvider(this).get(BudgetPlanningViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Inicijalizacija UI komponenti
        eventTypeSpinner = view.findViewById(R.id.spinner_event_type);
        showSuggestedCategoriesButton = view.findViewById(R.id.btn_suggested_categories);
        actionButton = view.findViewById(R.id.btn_action);
        titleTextView = view.findViewById(R.id.tv_title);
        budgetItemsRecyclerView = view.findViewById(R.id.rv_budget_items);
        addItemFab = view.findViewById(R.id.fab_add_item);

        // Postavi RecyclerView
        budgetItemAdapter = new BudgetItemAdapter(Collections.emptyList());
        budgetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        budgetItemsRecyclerView.setAdapter(budgetItemAdapter);

        setupViewsByType();
        setupObservers();
        setupListeners();

        return view;
    }
    private void setupListeners() {
        // Postavi onClickListener za FAB
        addItemFab.setOnClickListener(v -> {
            // ... (logika za dodavanje stavke)
        });

        // Ažuriraj listener za dugme za predložene kategorije
        showSuggestedCategoriesButton.setOnClickListener(v -> {
            if ("UPDATE".equalsIgnoreCase(type)) {
                viewModel.getBudgetDetails().observe(getViewLifecycleOwner(), budgetDetails -> {
                    if (budgetDetails != null && budgetDetails.getEventType() != null && budgetDetails.getEventType().getSuggestedCategoryNames() != null) {
                        showSuggestedCategoriesDialog(budgetDetails.getEventType().getSuggestedCategoryNames());
                    }
                });
            } else if ("CREATE".equalsIgnoreCase(type)) {
                // Posmatraj predložene kategorije i prikaži dijalog kada podaci stignu
                viewModel.getSuggestedCategories().observe(getViewLifecycleOwner(), suggestedCategories -> {
                    if (suggestedCategories != null) {
                        showSuggestedCategoriesDialog(suggestedCategories);
                    }
                });
            }
        });

        // Listener za promjenu odabira u Spinneru
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("CREATE".equalsIgnoreCase(type)) {
                    String selectedEventType = (String) parent.getItemAtPosition(position);
                    // Dohvati predložene kategorije za odabrani tip eventa
                    viewModel.fetchSuggestedCategoriesForEventType(selectedEventType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne radimo ništa
            }
        });
    }
    // Dodaj novu metodu za prikaz dijaloga
    private void showSuggestedCategoriesDialog(List<String> suggestedCategories) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Predložene kategorije")
                .setItems(suggestedCategories.toArray(new String[0]), (dialog, which) -> {
                    // Možeš dodati logiku za odabir kategorije ovde, ako je potrebno
                })
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        // Ako nema predloženih kategorija, prikaži poruku
        if (suggestedCategories.isEmpty()) {
            builder.setMessage("Nema predloženih kategorija za ovaj tip događaja.");
        }

        builder.create().show();
    }

    private void setupViewsByType() {
        if ("CREATE".equalsIgnoreCase(type)) {
            titleTextView.setText("Planiranje budžeta");
            actionButton.setText("Kreiraj događaj");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);
            // Pozivamo metodu za dohvat svih aktivnih tipova eventa
            viewModel.fetchActiveEventTypes();
        } else if ("UPDATE".equalsIgnoreCase(type)) {
            titleTextView.setText("Ažuriranje budžeta");
            actionButton.setText("Ažuriraj");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);

            // Pozivamo metodu za dohvat budžeta
            if (eventId != null) {
                viewModel.fetchBudgetDetails(eventId);
            }
        }
    }

    private void setupObservers() {
        // Posmatraj promjene u budžetu
        viewModel.getBudgetDetails().observe(getViewLifecycleOwner(), budgetDetails -> {
            if (budgetDetails != null) {
                // Ažuriraj spinner
                if (budgetDetails.getEventType() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item,
                            Collections.singletonList(budgetDetails.getEventType().getName()));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventTypeSpinner.setAdapter(adapter);
                    eventTypeSpinner.setEnabled(false); // Onemogući promjenu tipa eventa
                }

                // **Ažuriraj RecyclerView s novim podacima**
                budgetItemAdapter.setItems(budgetDetails.getBudgetItems());
            }
        });

        // Posmatraj aktivne tipove eventa (režim kreiranja)
        viewModel.getActiveEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null && !eventTypes.isEmpty()) {
                List<String> eventTypeNames = eventTypes.stream()
                        .map(GetEventTypeDTO::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item,
                        eventTypeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eventTypeSpinner.setAdapter(adapter);
                eventTypeSpinner.setEnabled(true);
            }
        });

        // Posmatraj greške
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}