package com.example.eventplanner.fragments.budgetplanning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.budget.SolutionsAdapter;
import com.example.eventplanner.dto.budget.GetPurchaseAndReservationForBudgetDTO;

import java.util.ArrayList;
import java.util.List;

public class SolutionsListDialogFragment extends DialogFragment {

    private static final String ARG_SOLUTIONS = "solutions";

    private List<GetPurchaseAndReservationForBudgetDTO> solutions;

    public static SolutionsListDialogFragment newInstance(List<GetPurchaseAndReservationForBudgetDTO> solutions) {
        SolutionsListDialogFragment fragment = new SolutionsListDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOLUTIONS, (ArrayList<GetPurchaseAndReservationForBudgetDTO>) solutions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            solutions = (List<GetPurchaseAndReservationForBudgetDTO>) getArguments().getSerializable(ARG_SOLUTIONS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solutions_list_dialog, container, false);

        RecyclerView solutionsRecyclerView = view.findViewById(R.id.rv_solutions);
        solutionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SolutionsAdapter adapter = new SolutionsAdapter(solutions);
        solutionsRecyclerView.setAdapter(adapter);
        ImageView exitButton = view.findViewById(R.id.iv_exit_dialog);
        exitButton.setOnClickListener(v -> dismiss());

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.drawable.form_frame_white);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(layoutParams);
            }
        }
    }
}