package com.example.eventplanner.fragments.others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.fragments.auth.LoginFragment;
import com.example.eventplanner.dto.auth.PasswordChangeRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordFragment extends DialogFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button saveButton = view.findViewById(R.id.savePassBtn);
        saveButton.setOnClickListener(v -> {
            callChangePassword();

        });

        return view;
    }







    private void changePassword(PasswordChangeRequest request) {

        String token = getAuthToken();

        if (token == null) {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = ClientUtils.authService.changePassword("Bearer " + token, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Password changed successfully!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove("token").apply();

                    sharedPreferences.edit().putString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString()).apply();

                    Intent intent = new Intent(requireActivity(), HomepageActivity.class);
                    intent.putExtra("showLogin", true);
                    startActivity(intent);

                    dismiss();

                }

                else if (response.code() == 403) {
                    Toast.makeText(getActivity(), "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed password change!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void callChangePassword() {
        EditText oldPassField = view.findViewById(R.id.oldPass);
        EditText newPassField = view.findViewById(R.id.newPass);
        EditText confirmPassField = view.findViewById(R.id.confirmPass);

        if (!ValidationUtils.isFieldValid(oldPassField, "Old password is required!")) {
            return;
        }

        if (!ValidationUtils.isFieldValid(newPassField, "New password is required!")) {
            return;
        }

        if (!ValidationUtils.isFieldValid(confirmPassField, "Confirmation is required")) {
            return;
        }

        if (!ValidationUtils.isMatchingPassword(newPassField, confirmPassField)) {
            return;
        }

        String oldPass = oldPassField.getText().toString();
        String newPass = newPassField.getText().toString();

        PasswordChangeRequest request = new PasswordChangeRequest(oldPass, newPass);
        changePassword(request);
    }


    private String getAuthToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

}