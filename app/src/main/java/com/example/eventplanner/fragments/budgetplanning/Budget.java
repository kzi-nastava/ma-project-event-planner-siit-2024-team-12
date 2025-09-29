package com.example.eventplanner.fragments.budgetplanning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.budget.BudgetItemAdapter;
import com.example.eventplanner.dto.budget.CreateBudgetItemDTO;
import com.example.eventplanner.dto.budget.GetBudgetItemDTO;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.dto.solutioncategory.GetCategoryDTO;
import com.example.eventplanner.viewmodels.BudgetPlanningViewModel;
import com.example.eventplanner.viewmodels.EventCreationViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Budget extends Fragment implements BudgetItemDialogFragment.BudgetItemDialogListener{

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
    String eventTypeName;

    public Budget() {
    }
    @Override
    public void onBudgetItemAdded(GetBudgetItemDTO newItem, GetCategoryDTO selectedCategory) {
        boolean categoryExists = false;
        for (GetBudgetItemDTO item : budgetItemAdapter.getItems()) {
            if (item.getCategory() != null && item.getCategory().getId().equals(selectedCategory.getId())) {
                categoryExists = true;
                break;
            }
        }
        if (categoryExists) {
            Toast.makeText(getContext(), "An item with the selected category already exists.", Toast.LENGTH_LONG).show();
        } else {
            budgetItemAdapter.addItem(newItem);
        }
    }
    @Override
    public void onBudgetItemUpdated(GetBudgetItemDTO updatedItem, GetCategoryDTO selectedCategory, int position) {
        boolean categoryExists = false;
        for (int i = 0; i < budgetItemAdapter.getItems().size(); i++) {
            GetBudgetItemDTO item = budgetItemAdapter.getItems().get(i);
            if (i != position && item.getCategory() != null && item.getCategory().getId().equals(selectedCategory.getId())) {
                categoryExists = true;
                break;
            }
        }
        if (categoryExists) {
            Toast.makeText(getContext(), "An item with the selected category already exists.", Toast.LENGTH_LONG).show();
        } else {
            budgetItemAdapter.updateItem(updatedItem, position);
        }
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

        eventTypeSpinner = view.findViewById(R.id.spinner_event_type);
        showSuggestedCategoriesButton = view.findViewById(R.id.btn_suggested_categories);
        actionButton = view.findViewById(R.id.btn_action);
        titleTextView = view.findViewById(R.id.tv_title);
        budgetItemsRecyclerView = view.findViewById(R.id.rv_budget_items);
        addItemFab = view.findViewById(R.id.fab_add_item);

        budgetItemAdapter = new BudgetItemAdapter(new ArrayList<>());

        budgetItemAdapter.setOnItemActionListener(new BudgetItemAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(GetBudgetItemDTO item, int position) {
                BudgetItemDialogFragment dialogFragment = BudgetItemDialogFragment.newInstance(item, position);
                dialogFragment.setBudgetItemDialogListener(Budget.this);
                dialogFragment.show(getParentFragmentManager(), "EditBudgetItemDialog");
            }

            @Override
            public void onDeleteClick(GetBudgetItemDTO item, int position) {
                if (item.getSolutions() != null && !item.getSolutions().isEmpty()) {
                    Toast.makeText(getContext(), "Item has purchased solutions. Cannot be deleted", Toast.LENGTH_LONG).show();
                } else {
                    showDeleteConfirmationDialog(item, position);
                }
            }

            @Override
            public void onViewSolutionsClick(GetBudgetItemDTO item, int position) {
                showSolutionsDialog(item);
            }
        });
        budgetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        budgetItemsRecyclerView.setAdapter(budgetItemAdapter);

        setupViewsByType();
        setupObservers();
        setupListeners();

        return view;
    }
    private void setupListeners() {
        addItemFab.setOnClickListener(v -> {
            BudgetItemDialogFragment dialogFragment = BudgetItemDialogFragment.newInstance();
            dialogFragment.setBudgetItemDialogListener(this);
            dialogFragment.show(getParentFragmentManager(), "BudgetItemDialog");
        });

        showSuggestedCategoriesButton.setOnClickListener(v -> {
            if ("UPDATE".equalsIgnoreCase(type)) {
                viewModel.getBudgetDetails().observe(getViewLifecycleOwner(), budgetDetails -> {
                    if (budgetDetails != null && budgetDetails.getEventType() != null && budgetDetails.getEventType().getSuggestedCategoryNames() != null) {
                        showSuggestedCategoriesDialog(budgetDetails.getEventType().getSuggestedCategoryNames());
                    }
                });
            } else if ("CREATE".equalsIgnoreCase(type)) {
                viewModel.getSuggestedCategories().observe(getViewLifecycleOwner(), suggestedCategories -> {
                    if (suggestedCategories != null) {
                        showSuggestedCategoriesDialog(suggestedCategories);
                    }
                });
            }
        });
        actionButton.setOnClickListener(v -> {
            if ("UPDATE".equalsIgnoreCase(type)) {
                List<GetBudgetItemDTO> currentItems = budgetItemAdapter.getItems();
                viewModel.updateBudget(currentItems, eventId);
            } else if ("CREATE".equalsIgnoreCase(type)) {
                EventCreationViewModel eventViewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);
                List<GetBudgetItemDTO> currentItems = budgetItemAdapter.getItems();
                List<CreateBudgetItemDTO> createItems = new ArrayList<>();
                for (GetBudgetItemDTO item : currentItems){
                    CreateBudgetItemDTO createItem = new CreateBudgetItemDTO();
                    createItem.setCost(item.getCost());
                    createItem.setCategoryId(Long.parseLong(item.getCategory().getId()));
                    createItem.setName(item.getName());
                    createItems.add(createItem);
                }
                eventViewModel.setBudget(createItems);

                SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String userEmail = prefs.getString("email", null);


                eventViewModel.updateEventAttributes("eventType", eventTypeName);
                eventViewModel.updateEventAttributes("organizer", userEmail);
                eventViewModel.createEventAndHandleCallback(getContext(),
                        () -> {
                            getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        },
                        () -> {
                            Log.i("UI_ACTION", "Event creation failed, user notified.");
                        }
                );
            }
        });

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("CREATE".equalsIgnoreCase(type)) {
                    String selectedEventType = (String) parent.getItemAtPosition(position);
                    eventTypeName = selectedEventType;
                    viewModel.fetchSuggestedCategoriesForEventType(selectedEventType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void showDeleteConfirmationDialog(GetBudgetItemDTO item, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm deletion")
                .setMessage("Are you sure you want to delete the item '" + item.getName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    budgetItemAdapter.removeItem(position);
                    Toast.makeText(getContext(), "Item deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void showSolutionsDialog(GetBudgetItemDTO item) {
        SolutionsListDialogFragment solutionsDialog = SolutionsListDialogFragment.newInstance(item.getSolutions());
        solutionsDialog.show(getParentFragmentManager(), "SolutionsDialog");
    }
    private void showSuggestedCategoriesDialog(List<String> suggestedCategories) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Suggested categories")
                .setItems(suggestedCategories.toArray(new String[0]), (dialog, which) -> {
                })
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        if (suggestedCategories.isEmpty()) {
            builder.setMessage("No suggested categories for this event type.");
        }

        builder.create().show();
    }

    private void setupViewsByType() {
        if ("CREATE".equalsIgnoreCase(type)) {
            titleTextView.setText("Budget planning");
            actionButton.setText("Create event");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);
            viewModel.fetchActiveEventTypes();
        } else if ("UPDATE".equalsIgnoreCase(type)) {
            titleTextView.setText("Budget planning");
            actionButton.setText("Update");
            budgetItemsRecyclerView.setVisibility(View.VISIBLE);

            if (eventId != null) {
                viewModel.fetchBudgetDetails(eventId);
            }
        }
    }

    private void setupObservers() {
        viewModel.getBudgetDetails().observe(getViewLifecycleOwner(), budgetDetails -> {
            if (budgetDetails != null) {
                if (budgetDetails.getEventType() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item,
                            Collections.singletonList(budgetDetails.getEventType().getName()));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventTypeSpinner.setAdapter(adapter);
                    eventTypeSpinner.setEnabled(false);
                }
                budgetItemAdapter.setItems(budgetDetails.getBudgetItems());
            }
        });

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
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Budget updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });

        viewModel.getUpdatedItems().observe(getViewLifecycleOwner(), updatedItems -> {
            if (updatedItems != null) {
                budgetItemAdapter.setItems(updatedItems);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}