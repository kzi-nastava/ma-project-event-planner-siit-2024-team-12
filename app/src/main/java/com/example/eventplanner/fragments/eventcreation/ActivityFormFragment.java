package com.example.eventplanner.fragments.eventcreation;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventDetailsDTO;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.agenda.CreateActivityDTO;
import com.example.eventplanner.viewmodels.EventCreationViewModel;
import com.example.eventplanner.viewmodels.EventEditViewModel;


public class ActivityFormFragment extends DialogFragment {
    private View view;
    private EventCreationViewModel viewModel;
    private EventEditViewModel editViewModel;
    private Boolean isEditable;
    private EditText timeField, descriptionField, venueField, nameField;
    private Integer position;


    public ActivityFormFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static ActivityFormFragment newInstance(Boolean isEditable, CreateActivityDTO activity) {
        Bundle args = new Bundle();
        args.putSerializable("is_editable", isEditable);
        args.putSerializable("activity_data", activity);

        ActivityFormFragment fragment = new ActivityFormFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public static ActivityFormFragment newEditInstance(Boolean isEditable, CreateActivityDTO activity, Integer position) {
        Bundle args = new Bundle();
        args.putSerializable("is_editable", isEditable);
        args.putSerializable("activity_data", activity);
        args.putSerializable("position", position);

        ActivityFormFragment fragment = new ActivityFormFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_activity_form, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EventCreationViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EventEditViewModel.class);

        findViews();

        Button addBtn = view.findViewById(R.id.addBtn);

        if (getArguments() != null) {
            isEditable = (Boolean) getArguments().getSerializable("is_editable");
            CreateActivityDTO activity = (CreateActivityDTO) getArguments().getSerializable("activity_data");
            position = getArguments().containsKey("position") ? (Integer) getArguments().getSerializable("position") : -1;

            if (activity != null) {
                timeField.setText(activity.getTime());
                descriptionField.setText(activity.getDescription());
                venueField.setText(activity.getLocation());
                nameField.setText(activity.getName());

                addBtn.setText(getString(R.string.edit));

            }
        }

        addBtn.setOnClickListener(v -> {
            addActivity();
        });

        return view;
    }


    private void findViews() {
        timeField = view.findViewById(R.id.time);
        descriptionField = view.findViewById(R.id.description);
        venueField = view.findViewById(R.id.venue);
        nameField = view.findViewById(R.id.name);
    }


    private boolean validateFields() {
        if (!ValidationUtils.isFieldValid(timeField, "Time is required!")) return false;
        if (!ValidationUtils.isActivityTimeValid(timeField)) return false;
        if (!ValidationUtils.isFieldValid(nameField, "Name is required!")) return false;
        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required!")) return false;
        if (!ValidationUtils.isFieldValid(venueField, "Venue is required!")) return false;

        return true;
    }


    private void addActivity() {
        // validate input data
        if (!validateFields()) {
            return;
        }

        // if valid, save
        String time = timeField.getText().toString();
        String description = descriptionField.getText().toString();
        String venue = venueField.getText().toString();
        String name = nameField.getText().toString();

        CreateActivityDTO newActivity = new CreateActivityDTO(time, name, description, venue);

        if (isEditable) {
            editViewModel.updateAgenda(newActivity, position >= 0 ? position : null);
            dismiss();
        }
        else {
            viewModel.updateAgenda(newActivity);
            dismiss();
        }
    }





    public void closeForm(View view) {
        dismiss();
    }

}