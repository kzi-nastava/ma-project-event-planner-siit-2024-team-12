package com.example.eventplanner.fragments.businessregistration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BusinessRegistrationActivity;

import java.io.IOException;


public class BusinessRegistration2 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView selectedImage;
    private Button uploadImageButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_registration2, container, false);

        String registrationSuccess = getString(R.string.registration_success);

        Button backButton = view.findViewById(R.id.back2);
        Button registerButton = view.findViewById(R.id.register);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof BusinessRegistrationActivity) {
                ((BusinessRegistrationActivity) getActivity()).previousPage();
            }
        });

        registerButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), registrationSuccess, Toast.LENGTH_SHORT).show();

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
}