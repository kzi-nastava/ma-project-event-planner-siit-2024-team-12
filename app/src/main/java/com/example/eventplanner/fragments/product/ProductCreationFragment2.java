package com.example.eventplanner.fragments.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.product.ProvidedProductsActivity;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.utils.ValidationUtils;
import com.example.eventplanner.viewmodels.ProductCreationViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductCreationFragment2 extends DialogFragment {

    private View view;
    private ProductCreationViewModel viewModel;
    private EditText priceField, discountField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_creation2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ProductCreationViewModel.class);

        Button backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            dismiss();
        });

        Button createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(v -> {
            createProduct();
        });

        setupRadioButtons();

        return view;
    }



    private void saveProduct() {
        String auth = ClientUtils.getAuthorization(requireContext());

        Call<ResponseBody> call = ClientUtils.productService.createProduct(auth, viewModel.getDto().getValue());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Successfully created product!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), ProvidedProductsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), "Error creating product!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to create product!", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void saveSecondForm() {
        priceField = view.findViewById(R.id.price);
        discountField = view.findViewById(R.id.discount);

        if (!ValidationUtils.isFieldValid(priceField, "Price is required!")) return;
        if (!ValidationUtils.isNumberValid(priceField, "Enter a number!", "Negative number!")) return;
        if (!ValidationUtils.isFieldValid(discountField, "Discount is required!")) return;
        if (!ValidationUtils.isNumberValid(discountField, "Enter a number!", "Negative number!")) return;

        viewModel.updateAttributes("price", priceField.getText().toString());
        viewModel.updateAttributes("discount", discountField.getText().toString());
        viewModel.updateAttributes("available", String.valueOf(viewModel.isAvailable()));
        viewModel.updateAttributes("visible", String.valueOf(viewModel.isVisible()));

        saveProduct();

    }


    private void createProduct() {
        saveSecondForm();
    }





    private void setupRadioButtons() {
        RadioGroup availabilityGroup = view.findViewById(R.id.availabilityGroup);

        RadioGroup visibilityGroup = view.findViewById(R.id.visibilityGroup);

        // Set initial values from ViewModel
        availabilityGroup.check(viewModel.isAvailable() ? R.id.available : R.id.unavailable);
        visibilityGroup.check(viewModel.isVisible() ? R.id.visible : R.id.invisible);

        availabilityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.available) {
                viewModel.setAvailable(true);
            } else {
                viewModel.setAvailable(false);
            }
        });

        visibilityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.visible) {
                viewModel.setVisible(true);
            } else {
                viewModel.setVisible(false);
            }
        });
    }

}