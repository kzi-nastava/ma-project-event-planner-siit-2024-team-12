package com.example.eventplanner.fragments.servicecreation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.eventplanner.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceCreation2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceCreation2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceCreation2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceCreation2.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceCreation2 newInstance(String param1, String param2) {
        ServiceCreation2 fragment = new ServiceCreation2();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_creation2, container, false);
        Button nextButton = view.findViewById(R.id.nextServiceCreate2);
        Button backButton = view.findViewById(R.id.backServiceCreate);
        ImageView xButton = view.findViewById(R.id.imageView5);

        nextButton.setOnClickListener(v -> {
            if(validateForm()){
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof ServiceCreationContainer) {
                    ((ServiceCreationContainer) parentFragment).nextPage();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof ServiceCreationContainer) {
                ((ServiceCreationContainer) parentFragment).previousPage();
            }
        });

        xButton.setOnClickListener(v ->{
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public boolean validateForm() {
        EditText priceEditText = requireView().findViewById(R.id.editTextTextEdit);
        EditText discountEditText = requireView().findViewById(R.id.editTextTextEdit2);
        EditText resDeadlineEditText = requireView().findViewById(R.id.editTextText4);
        EditText cancelDeadlineEditText = requireView().findViewById(R.id.editTextText5);

        if (priceEditText.getText().toString().trim().isEmpty()) {
            priceEditText.setError("Cena ne može biti prazna.");
            return false;
        }
        if (discountEditText.getText().toString().trim().isEmpty()) {
            discountEditText.setError("Popust ne može biti prazan.");
            return false;
        }
        if (resDeadlineEditText.getText().toString().trim().isEmpty()) {
            resDeadlineEditText.setError("Rok za rezervaciju ne može biti prazan.");
            return false;
        }
        if (cancelDeadlineEditText.getText().toString().trim().isEmpty()) {
            cancelDeadlineEditText.setError("Rok za otkazivanje ne može biti prazan.");
            return false;
        }

        int price;
        int discount;
        int resDeadline;
        int cancelDeadline;

        try {
            price = Integer.parseInt(priceEditText.getText().toString().trim());
            discount = Integer.parseInt(discountEditText.getText().toString().trim());
            resDeadline = Integer.parseInt(resDeadlineEditText.getText().toString().trim());
            cancelDeadline = Integer.parseInt(cancelDeadlineEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            priceEditText.setError("Unesite ispravne brojeve.");
            return false;
        }

        if (price < 1) {
            priceEditText.setError("Cena mora biti najmanje 1.");
            return false;
        }

        if (discount < 0 || discount > 99) {
            discountEditText.setError("Popust mora biti između 0 i 99.");
            return false;
        }

        if (resDeadline <= cancelDeadline) {
            resDeadlineEditText.setError("Rok za rezervaciju mora biti duži od roka za otkazivanje.");
            return false;
        }

        return true;
    }
}