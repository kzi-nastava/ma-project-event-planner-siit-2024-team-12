package com.example.eventplanner.fragments.profile;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.business.GetBusinessAndProviderDTO;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.viewmodels.BusinessProviderViewModel;

public class BusinessProviderDialogFragment extends DialogFragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_SOLUTION_ID = "solutionId";

    private String type;
    private Long solutionId;

    private BusinessProviderViewModel viewModel;
    private ImageView ivCompanyImage;
    private TextView tvCompanyName;
    private TextView tvCompanyEmail;
    private TextView tvCompanyPhone;
    private TextView tvProviderPhone;

    private ImageView ivProviderImage;
    private TextView tvProviderNameSurname;
    private TextView tvProviderEmail;


    public static BusinessProviderDialogFragment newInstance(String type, Long solutionId) {
        BusinessProviderDialogFragment fragment = new BusinessProviderDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putLong(ARG_SOLUTION_ID, solutionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            solutionId = getArguments().getLong(ARG_SOLUTION_ID);
        }
        viewModel = new ViewModelProvider(this).get(BusinessProviderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_provider_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCompanyImage = view.findViewById(R.id.iv_company_image);
        tvCompanyName = view.findViewById(R.id.tv_company_name);
        tvCompanyEmail = view.findViewById(R.id.tv_company_email);
        tvCompanyPhone = view.findViewById(R.id.tv_company_phone);
        tvProviderPhone = view.findViewById(R.id.tv_provider_phone);
        ivProviderImage = view.findViewById(R.id.iv_provider_image);
        tvProviderNameSurname = view.findViewById(R.id.tv_provider_name_surname);
        tvProviderEmail = view.findViewById(R.id.tv_provider_email);

        if (solutionId != null && type != null) {
            viewModel.fetchBusinessProviderDetails(type, solutionId);
            setupObservers();
        } else {
            Toast.makeText(getContext(), "Missing type or solution ID.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void setupObservers() {
        viewModel.getBusinessProviderDetails().observe(getViewLifecycleOwner(), dto -> {
            if (dto != null) {
                displayDetails(dto);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                Log.e("Dialog", "API Error: " + errorMessage);
                dismiss();
            }
        });
    }

    private void displayDetails(GetBusinessAndProviderDTO dto) {
        tvCompanyName.setText(dto.getCompanyName());
        tvCompanyEmail.setText(dto.getCompanyEmail());
        tvCompanyPhone.setText(dto.getCompanyPhone());
        tvProviderPhone.setText(dto.getProviderPhone());

        Glide.with(this)
                .load(ClientUtils.BASE_IMAGE_URL + dto.getCompanyMainImage())
                .placeholder(R.drawable.ic_business)
                .error(R.drawable.ic_business)
                .into(ivCompanyImage);

        String providerFullName = dto.getProviderName() + " " + dto.getProviderSurname();
        tvProviderNameSurname.setText(providerFullName);
        tvProviderEmail.setText(dto.getProviderEmail());

        Glide.with(this)
                .load(ClientUtils.BASE_IMAGE_URL + dto.getProviderMainImage())
                .placeholder(R.drawable.user_logo)
                .error(R.drawable.user_logo)
                .into(ivProviderImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
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