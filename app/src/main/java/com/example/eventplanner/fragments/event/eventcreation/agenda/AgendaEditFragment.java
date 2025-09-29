package com.example.eventplanner.fragments.event.eventcreation.agenda;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.fragments.budgetplanning.Budget;
import com.example.eventplanner.fragments.budgetplanning.BudgetPlanningFragment;
import com.example.eventplanner.fragments.event.eventcreation.EventCreation2;
import com.example.eventplanner.model.Activity;
import com.example.eventplanner.viewmodels.EventCreationViewModel;
import com.example.eventplanner.viewmodels.EventEditViewModel;

import java.util.ArrayList;


public class AgendaEditFragment extends DialogFragment {

    private ArrayList<Activity> activities;
    View view;
    private EventDetailsDTO detailsDTO;
    private EventEditViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agenda_edit, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventEditViewModel.class);


        // retrieve event details from Bundle
        if (getArguments() != null) {
            detailsDTO = (EventDetailsDTO) getArguments().getSerializable("passed_details");
        }

        // pass event details to agenda table
        AgendaTableFragment agendaTableFragment = new AgendaTableFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("is_editable", true);
        bundle.putSerializable("passed_details", detailsDTO);
        agendaTableFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.eventTypeFragmentContainer, agendaTableFragment)
                .commit();

        setUpActivityBtn();

        setUpSaveBtn();

        return view;
    }


    private void setUpSaveBtn() {
        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            dismiss();
        });
    }


    private void setUpActivityBtn() {
        Button activityBtn = view.findViewById(R.id.activityBtn);

        activityBtn.setOnClickListener(v -> {
            ActivityFormFragment activityForm = ActivityFormFragment.newInstance(true, null);
            activityForm.show(getChildFragmentManager(), "ActivityForm");
        });
    }


    public static AgendaEditFragment newInstance(EventDetailsDTO detailsDTO) {
        AgendaEditFragment fragment = new AgendaEditFragment();
        Bundle args = new Bundle();
        args.putSerializable("passed_details", detailsDTO);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    public static class AgendaFragment extends Fragment {
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


            Budget budgetFragment = Budget.newInstance("CREATE", null);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, budgetFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

    }
}
