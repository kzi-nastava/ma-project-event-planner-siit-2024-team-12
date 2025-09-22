package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Budget extends Fragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_EVENT_ID = "eventId";

    private String type;
    private Long eventId;

    private Spinner eventTypeSpinner;
    private ImageButton showSuggestedCategoriesButton;
    private Button actionButton;
    private TextView titleTextView;
    private RecyclerView budgetItemsRecyclerView;
    private FloatingActionButton addItemFab;

    public Budget() {
        // Obavezni prazan konstruktor
    }

    /**
     * Factory metoda za kreiranje instance fragmenta.
     *
     * @param type    "CREATE" ili "UPDATE"
     * @param eventId ID eventa (opciono, samo za UPDATE)
     * @return Nova instanca fragmenta BudgetPlanning.
     */
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
        budgetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter ćeš kasnije kreirati i postaviti ovdje

        setupViewsByType();

        // Postavi onClickListener za FAB
        addItemFab.setOnClickListener(v -> {
            // Ovde ćeš pokrenuti dijalog ili fragment za dodavanje nove stavke budžeta
            // Na primer, pozvati metodu showAddItemDialog()
        });

        // Ovde dodaj listener za dugme za predložene kategorije
        showSuggestedCategoriesButton.setOnClickListener(v -> {
            // Ovde ćeš pokrenuti AlertDialog sa listom predloženih kategorija
        });

        return view;
    }

    private void setupViewsByType() {
        if ("CREATE".equalsIgnoreCase(type)) {
            // Režim kreiranja
            titleTextView.setText("Planiranje budžeta");
            actionButton.setText("Kreiraj događaj");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);

        } else if ("UPDATE".equalsIgnoreCase(type)) {
            // Režim ažuriranja
            titleTextView.setText("Ažuriranje budžeta");
            actionButton.setText("Ažuriraj");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}