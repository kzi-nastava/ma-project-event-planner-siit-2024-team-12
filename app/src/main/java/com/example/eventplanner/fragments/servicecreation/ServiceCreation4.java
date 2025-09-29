package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.ServiceCreationViewModel;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation4 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ServiceCreationViewModel viewModel;

    private ImageView serviceImagePreview;
    private ImageButton selectImageButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation4.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation4 newInstance(String param1, String param2) {
        ServiceCreation4 fragment = new ServiceCreation4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(ServiceCreationViewModel.class);
        viewModel.setServiceImageUri(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_creation4, container, false);
        Button backButton = view.findViewById(R.id.backServiceCreate4);
        Button submitButton = view.findViewById(R.id.submitService);
        ImageView xButton = view.findViewById(R.id.imageView5);

        String serviceCreatedMessage = getString(R.string.service_created);

        backButton.setOnClickListener(v -> {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof ServiceCreationContainer) {
                ((ServiceCreationContainer) parentFragment).previousPage();
            }
        });

        submitButton.setOnClickListener(v -> {
            if(validateForm()){
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof ServiceCreationContainer) {
                    ((ServiceCreationContainer) parentFragment).nextPage();
                }
            }
        });

        xButton.setOnClickListener(v ->{
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        serviceImagePreview = view.findViewById(R.id.service_image_preview);
        selectImageButton = view.findViewById(R.id.select_image_button);

        viewModel.getServiceImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                serviceImagePreview.setImageURI(uri);
            }
        });

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        return view;
    }
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    viewModel.setServiceImageUri(selectedImageUri);
                    serviceImagePreview.setImageURI(selectedImageUri);
                }
            });

    public boolean validateForm() {
        EditText serviceDescriptionEditText = requireView().findViewById(R.id.service_description_edittext);
        EditText serviceSpecificitiesEditText = requireView().findViewById(R.id.service_specificities_edittext);

        if (serviceDescriptionEditText.getText().toString().trim().isEmpty()) {
            serviceDescriptionEditText.setError("Opis usluge ne može biti prazan.");
            return false;
        }

        if (serviceSpecificitiesEditText.getText().toString().trim().isEmpty()) {
            serviceSpecificitiesEditText.setError("Specifičnosti ne mogu biti prazne.");
            return false;
        }
        if (viewModel.getServiceImageUri().getValue() == null) {
            Toast.makeText(requireContext(), "Molimo odaberite sliku za uslugu.", Toast.LENGTH_SHORT).show();
            return false;
        }
        viewModel.addData("description", serviceDescriptionEditText.getText().toString().trim());
        viewModel.addData("specifics", serviceSpecificitiesEditText.getText().toString().trim());

        return true;
    }
}