package com.example.eventplanner.fragments.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.fragments.auth.SignUpFragment;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.fragments.homepage.HomepageFragment;
import com.example.eventplanner.fragments.others.ChangePasswordFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        getCurrentUser();

        Button deactivateBtn = view.findViewById(R.id.deactivateButton);
        deactivateBtn.setOnClickListener(this::deactivateAccount);

        Button editBtn = view.findViewById(R.id.editButton);
        editBtn.setOnClickListener(this::openProfileEdit);

        return view;
    }


    public void openProfileEdit(View view) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new ProfileEditFragment())
                .commit();
    }


    public void deactivateAccount(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        dialog.setTitle("Deactivate account?");
        dialog.setMessage("Are you sure you want to deactivate your account?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deactivate();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }



    private void getCurrentUser() {
        String authorization = ClientUtils.getAuthorization(requireContext());

        Call<GetUserDTO> call = ClientUtils.authService.getCurrentUser(authorization);

        call.enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful()) {
                    GetUserDTO user = response.body();
                    Log.d("API_RESPONSE", "Response: " + new Gson().toJson(user));
                    saveUserRole(user.getRole());
                    setUpFormDetails(user);
                } else {
                    Toast.makeText(requireActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(requireActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setUpFormDetails(GetUserDTO getUserDTO) {
        String userRole = getUserRole();

        View fullProfileView = view.findViewById(R.id.full_profile_view);
        View authenticatedProfileView = view.findViewById(R.id.authenticated_profile_view);

        if (userRole.equals(UserRole.ROLE_AUTHENTICATED_USER.toString())) {
            if (fullProfileView != null) fullProfileView.setVisibility(View.GONE);
            if (authenticatedProfileView != null) authenticatedProfileView.setVisibility(View.VISIBLE);

            TextView emailAK = view.findViewById(R.id.emailAK);
            if (emailAK != null) emailAK.setText(getUserDTO.getEmail());

            Button upgradeRoleBtn = view.findViewById(R.id.upgradeRoleBtn);
            if (upgradeRoleBtn != null) {
                upgradeRoleBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), SignUpFragment.class);
                    intent.putExtra("IS_UPGRADE", true);
                    intent.putExtra("USER_EMAIL", getUserDTO.getEmail());
                    startActivity(intent);
                });
            }

            Button changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
            if (changePasswordBtn != null) {
                changePasswordBtn.setOnClickListener(v -> {
                    ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                    changePasswordFragment.show(getParentFragmentManager(), "change_password_fragment");
                });
            }

            Button logOutBtn = view.findViewById(R.id.logOutBtn);
            if (logOutBtn != null) {
                logOutBtn.setOnClickListener(v -> logOut());
            }


        } else {
            if (fullProfileView != null) fullProfileView.setVisibility(View.VISIBLE);
            if (authenticatedProfileView != null) authenticatedProfileView.setVisibility(View.GONE);

            TextView name = view.findViewById(R.id.name);
            if (name != null) name.setText(getUserDTO.getName());

            TextView surname = view.findViewById(R.id.surname);
            if (surname != null) surname.setText(getUserDTO.getSurname());

            TextView email = view.findViewById(R.id.email);
            if (email != null) email.setText(getUserDTO.getEmail());

            TextView address = view.findViewById(R.id.address);
            if (address != null && getUserDTO.getLocation() != null) {
                String fullAddress = getUserDTO.getLocation().getAddress() + ", " +
                        getUserDTO.getLocation().getCity() + ", " + getUserDTO.getLocation().getCountry();
                address.setText(fullAddress);
            }

            TextView phone = view.findViewById(R.id.phone);
            if (phone != null) phone.setText(getUserDTO.getPhone());

            ImageView mainImage = view.findViewById(R.id.mainImage);
            setMainImage(getUserDTO);
        }
    }



    private void setMainImage(GetUserDTO getUserDTO) {
        ImageView mainImage = view.findViewById(R.id.mainImage);
        String mainImageUrl = getUserDTO.getImageUrl();

        String fullUrl = "http://10.0.2.2:8080" + mainImageUrl;

        Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.user_logo)
                .error(R.drawable.user_logo)
                .into(mainImage);
    }



    private void saveUserRole(String userRole) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userRole", userRole);
        editor.apply();
    }

    private String getUserRole() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());
    }



    private void deactivate() {
        TextView emailField = view.findViewById(R.id.email);
        String email = emailField.getText().toString();

        String authorization = ClientUtils.getAuthorization(requireContext());

        Call<ResponseBody> call = ClientUtils.userService.deleteUser(authorization, email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Deactivated account!", Toast.LENGTH_SHORT).show();

                    saveUserRole(UserRole.ROLE_UNREGISTERED_USER.toString());

                    Intent intent = new Intent(requireActivity(), HomepageActivity.class);
                    startActivity(intent);
                }
                else if (response.code() == 403) {
                    Toast.makeText(requireActivity(), "Account cannot be deactivated!" +
                            " You have created future events.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to deactivate account!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOut() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("YES", (dialog, which) -> {

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("userRole");
                    editor.apply();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new HomepageFragment())
                            .commit();

                    requireActivity().finish();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}