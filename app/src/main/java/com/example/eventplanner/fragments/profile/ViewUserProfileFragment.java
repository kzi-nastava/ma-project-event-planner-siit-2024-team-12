package com.example.eventplanner.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewUserProfileFragment extends Fragment {

    private static final String ARG_USER_EMAIL = "user_email";
    private String userEmail;

    private TextView name, surname, email, phone, address;
    private ImageView mainImage, closeButton;
    private Button btnReportUser, btnBlockUser;

    public static ViewUserProfileFragment newInstance(String userEmail) {
        ViewUserProfileFragment fragment = new ViewUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_EMAIL, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_USER_EMAIL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_user_profile, container, false);

        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        mainImage = view.findViewById(R.id.mainImage);
        closeButton = view.findViewById(R.id.close_form);
        btnReportUser = view.findViewById(R.id.btnReportUser);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUserProfile();

        closeButton.setOnClickListener(v -> closeForm());
        btnReportUser.setOnClickListener(v -> reportUser());
        btnBlockUser.setOnClickListener(v -> blockUser());
    }

    private void fetchUserProfile() {
        String authorization = ClientUtils.getAuthorization(getContext());

        ClientUtils.userService.getUserProfile(authorization, userEmail).enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetUserDTO user = response.body();
                    setUpFormDetails(user);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    closeForm();
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                closeForm();
            }
        });
    }

    private void setUpFormDetails(GetUserDTO user) {
        name.setText(user.getName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        if (user.getLocation() != null) {
            String fullAddress = user.getLocation().getAddress() + ", " + user.getLocation().getCity() + ", " + user.getLocation().getCountry();
            address.setText(fullAddress);
        }
        setMainImage(user.getImageUrl());
    }

    private void setMainImage(String imageUrl) {
        if (getContext() != null) {
            String fullUrl = "http://10.0.2.2:8080" + imageUrl;
            Glide.with(getContext())
                    .load(fullUrl)
                    .placeholder(R.drawable.user_logo)
                    .error(R.drawable.user_logo)
                    .into(mainImage);
        }
    }

    private void closeForm() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void reportUser() {
        Toast.makeText(getContext(), "Report User action for email: " + userEmail, Toast.LENGTH_SHORT).show();
    }

    private void blockUser() {
        Toast.makeText(getContext(), "Block User action for email: " + userEmail, Toast.LENGTH_SHORT).show();
    }
}