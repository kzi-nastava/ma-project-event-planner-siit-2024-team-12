package com.example.eventplanner.fragments.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.eventplanner.R;

public class CategoryCreationFragment extends DialogFragment {

    public interface OnCategoryCreationListener {
        void onCategoryCreated();
        void onCategorySuggested();
        void onCategoryCreationCanceled();
    }

    private static final String ARG_CREATION_TYPE = "creation_type";
    private static final String TYPE_CREATE = "CREATE";
    private static final String TYPE_SUGGEST = "SUGGEST";

    private OnCategoryCreationListener listener;
    private String creationType;

    private EditText nameEditText;
    private EditText descriptionEditText;

    public static CategoryCreationFragment newInstance(String creationType) {
        CategoryCreationFragment fragment = new CategoryCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CREATION_TYPE, creationType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnCategoryCreationListener) {
            listener = (OnCategoryCreationListener) getParentFragment();
        } else {
            if (context instanceof OnCategoryCreationListener) {
                listener = (OnCategoryCreationListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " mora da implementira OnCategoryCreationListener");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            creationType = getArguments().getString(ARG_CREATION_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_creation, container, false);

        nameEditText = view.findViewById(R.id.editTextCategoryName);
        descriptionEditText = view.findViewById(R.id.editTextCategoryDesc);
        Button backButton = view.findViewById(R.id.backCategoryCreate);
        Button submitButton = view.findViewById(R.id.newCategorySubmit);
        TextView titleTextView = view.findViewById(R.id.textViewCat);

        if (TYPE_SUGGEST.equals(creationType)) {
            titleTextView.setText("Suggest Category");
            submitButton.setText("Suggest");
        } else {
            titleTextView.setText("Create Category");
            submitButton.setText("Create");
        }

        backButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryCreationCanceled();
            }
            dismiss();
        });

        submitButton.setOnClickListener(v -> {
            String categoryName = nameEditText.getText().toString();
            String categoryDescription = descriptionEditText.getText().toString();

            if (categoryName.isEmpty() || categoryDescription.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                if (listener != null) {
                    if (TYPE_SUGGEST.equals(creationType)) {
                        listener.onCategorySuggested();
                    } else {
                        listener.onCategoryCreated();
                    }
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            int dialogWidth = (int) (screenWidth * 0.8);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.form_frame_white);

            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}