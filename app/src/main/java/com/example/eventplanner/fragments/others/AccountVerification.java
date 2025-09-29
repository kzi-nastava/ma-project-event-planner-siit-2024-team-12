package com.example.eventplanner.fragments.others;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.auth.LoginFragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountVerification extends DialogFragment {
    private String email;

    public AccountVerification(String email) {
        this.email = email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_account, container, false);

        Button verifyBtn = view.findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(v -> {
            verifyAccount(email);
        });



        return view;
    }


    private void verifyAccount(String email) {
        Call<ResponseBody> call = ClientUtils.authService.verifyUserAccount(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Successful account verification!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginFragment.class);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(getActivity(), "Failed account verification!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed account verification!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}