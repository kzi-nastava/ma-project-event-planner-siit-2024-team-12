package com.example.eventplanner.fragments.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.activities.auth.LoginActivity;
import com.example.eventplanner.activities.auth.SignUpActivity;
import com.example.eventplanner.dto.user.CreateUserDTO;
import com.example.eventplanner.viewmodels.SignUpViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp3 extends Fragment {

    private String selectedRole = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up3, container, false);

        SignUpViewModel viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        Button backButton = view.findViewById(R.id.back3);
        Button submitButton = view.findViewById(R.id.next);
        Spinner mySpinner = view.findViewById(R.id.mySpinner);


        String[] rolesArray = getResources().getStringArray(R.array.roles_spinner);
        String providerRole = rolesArray[1];

        String registrationSuccess = getString(R.string.registration_success);


        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpActivity) {
                ((SignUpActivity) getActivity()).previousPage();
            }
        });



        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "";
            }
        });


        submitButton.setOnClickListener(v -> {
            EditText addressField = view.findViewById(R.id.address);
            EditText phoneField = view.findViewById(R.id.phone);

            if (!ValidationUtils.isFieldValid(addressField, "Address is required!")) return;
            if (!ValidationUtils.isAddressFormatValid(addressField)) return;
            if (!ValidationUtils.isFieldValid(phoneField, "Phone is required!")) return;
            if (!ValidationUtils.isPhoneValid(phoneField, phoneField.getText().toString())) return;

            viewModel.updateSignUpAttributes("address", addressField.getText().toString());
            viewModel.updateSignUpAttributes("phone", phoneField.getText().toString());

            String[] roles = getResources().getStringArray(R.array.roles_spinner);
            String selectedRole = mySpinner.getSelectedItem().toString();
            String role = "";

            if (selectedRole.equalsIgnoreCase(roles[0])) {
                role = UserRole.ROLE_ORGANIZER.toString();
            }
            else {
                role = UserRole.ROLE_PROVIDER.toString();
            }

            viewModel.updateSignUpAttributes("role", role);

            createUser(viewModel.getDto().getValue());

        });

        return view;
    }




    private void createUser(CreateUserDTO createUserDTO) {
        Call<ResponseBody> call = ClientUtils.authService.registerUser(createUserDTO);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "We've sent account activation link to" +
                            " your email address!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                }

                else if (response.code() == 409) {
                    Toast.makeText(getActivity(), "Already taken email address!", Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 410) {
                    Toast.makeText(getActivity(), "Your activation link is still valid! Check your email!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to register user!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
