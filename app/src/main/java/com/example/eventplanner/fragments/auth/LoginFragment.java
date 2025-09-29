package com.example.eventplanner.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.dto.auth.LogInRequest;
import com.example.eventplanner.dto.auth.UserTokenState;
import com.example.eventplanner.fragments.others.ResetPasswordFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends DialogFragment {

    private Button loginButton;
    private View view;
    private TextView signUp;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = view.findViewById(R.id.email);
                EditText passwordField = view.findViewById(R.id.password);

                if (!ValidationUtils.isFieldValid(emailField, "Email is required!")) return;
                if (!ValidationUtils.isEmailValid(emailField)) return;
                if (!ValidationUtils.isFieldValid(passwordField, "Password is required!")) return;

                logIn();
            }
        });

        signUp = view.findViewById(R.id.signUpTxt);
        signUp.setOnClickListener(this::openSignUp);

        return view;
    }


    public void openSignUp(View view) {
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.show(getParentFragmentManager(), "signUpFragment");
    }


    public void openResetPassword(View view) {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        resetPasswordFragment.show(getParentFragmentManager(), "resetPasswordFragment");
    }


    private LogInRequest getLogInRequest() {
        EditText emailField = view.findViewById(R.id.email);
        EditText passwordField = view.findViewById(R.id.password);

        return new LogInRequest(emailField.getText().toString(), passwordField.getText().toString());
    }

    private void logIn() {
        LogInRequest request = getLogInRequest();

        Call<ResponseBody> call = ClientUtils.authService.logIn(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        UserTokenState userToken = gson.fromJson(json, UserTokenState.class);
                        String token = userToken.getAccessToken();
                        DecodedJWT decodedJWT = JWT.decode(token);
                        String userRole = decodedJWT.getClaim("role").asString();
                        String email = decodedJWT.getClaim("sub").asString();

                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("userRole", userRole);
                        editor.putString("email", email);
                        editor.apply();

                        Intent intent = new Intent(requireActivity(), HomepageActivity.class);
                        startActivity(intent);
                        requireActivity().finish();

                    } catch (IOException e) {
                        Toast.makeText(requireActivity(), "Error parsing response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 403) {
                        try {
                            String json = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<Map<String, Object>>(){}.getType();
                            Map<String, Object> suspensionData = gson.fromJson(json, type);

                            double daysDouble = (double) suspensionData.get("days");
                            double hoursDouble = (double) suspensionData.get("hours");
                            double minutesDouble = (double) suspensionData.get("minutes");

                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isSuspended", true);
                            editor.putString("suspensionDays", String.format("%.0f", daysDouble));
                            editor.putString("suspensionHours", String.format("%.0f", hoursDouble));
                            editor.putString("suspensionMinutes", String.format("%.0f", minutesDouble));
                            editor.apply();

                            Intent intent = new Intent(requireActivity(), HomepageActivity.class);
                            startActivity(intent);
                            requireActivity().finish();

                        } catch (IOException e) {
                            Toast.makeText(requireActivity(), "Error processing suspension data.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        Toast.makeText(requireActivity(), "Wrong password!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(requireActivity(), "Inactive account!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.form_frame_white);

        }
    }

}