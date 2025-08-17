package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.budgetplanning.BudgetPlanningFragment;
import com.example.eventplanner.viewmodels.EventCreationViewModel;


public class AgendaFragment extends Fragment {
    EventCreationViewModel viewModel;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on back listener in case a user wants to change previously entered values
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                EventCreation2 eventCreation2 = new EventCreation2();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main, eventCreation2);
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agenda, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);

        Button activityBtn = view.findViewById(R.id.activityBtn);
        activityBtn.setOnClickListener(v -> {
            addActivity(view);
        });

        Button budgetBtn = view.findViewById(R.id.budgetBtn);
        budgetBtn.setOnClickListener(v -> {
            if (viewModel.isAgendaSet()) {
                planBudget();
            }
            else {
                Toast.makeText(getActivity(), "Add activity!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void addActivity(View view) {
        ActivityFormFragment activityFormFragment = ActivityFormFragment.newInstance(false, null);
        activityFormFragment.show(requireActivity().getSupportFragmentManager(), "addActivity");
    }


    private void planBudget() {


        BudgetPlanningFragment budgetPlanningFragment = new BudgetPlanningFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, budgetPlanningFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void closeForm(View view) {

    }

}