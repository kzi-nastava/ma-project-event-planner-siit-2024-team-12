package com.example.eventplanner.fragments.businessregistration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.ValidationUtils;
import com.example.eventplanner.activities.business.BusinessInfoActivity;
import com.example.eventplanner.activities.business.BusinessRegistrationActivity;
import com.example.eventplanner.viewmodels.BusinessViewModel;

import java.io.IOException;


public class BusinessRegistration2 extends Fragment {
    private View view;
    BusinessViewModel viewModel;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView selectedImage;
    private Button uploadImageButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_registration2, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        String registrationSuccess = getString(R.string.business_reg_success);

        Button backButton = view.findViewById(R.id.back2);
        Button registerButton = view.findViewById(R.id.register);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessRegistrationActivity) {
                ((BusinessRegistrationActivity) getActivity()).previousPage();
            }
        });

        registerButton.setOnClickListener(v -> {
            if (saveFormData()) {
                Intent intent = new Intent(getActivity(), BusinessInfoActivity.class);
                startActivity(intent);

                Toast.makeText(getActivity(), registrationSuccess, Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedImage = view.findViewById(R.id.selectedImage);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);

        uploadImageButton.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean saveFormData() {
        EditText phoneField = view.findViewById(R.id.phone);
        EditText descriptionField = view.findViewById(R.id.description);

        // validate input data
        if (!ValidationUtils.isFieldValid(phoneField, "Phone is required!")) return false;
        if (!ValidationUtils.isPhoneValid(phoneField, phoneField.getText().toString().trim())) return false;
        if (!ValidationUtils.isFieldValid(descriptionField, "Description is required")) return false;

        // if valid, save
        String phone = phoneField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();

        viewModel.update("phone", phone);
        viewModel.update("description", description);

        return true;

    }
}