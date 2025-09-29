package com.example.eventplanner.fragments.business;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.fragments.homepage.HomepageFragment;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.business.GetBusinessDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessInfoFragment extends Fragment {
    private String companyEmail;
    private View view;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_info, container, false);

        getCurrentBusiness();

        Button deactivateBtn = view.findViewById(R.id.deactivateButton);
        deactivateBtn.setOnClickListener(this::deactivateBusiness);

        Button editBtn = view.findViewById(R.id.editButton);
        editBtn.setOnClickListener(this::openBusinessEditFragment);

        return view;

    }


    public void deactivateBusiness(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        dialog.setTitle("Deactivate business?");
        dialog.setMessage("Are you sure you want to deactivate your business account?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deactivate();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }


    private void setUpFormDetails(GetBusinessDTO getBusinessDTO) {
        TextView name = view.findViewById(R.id.name);
        name.setText(getBusinessDTO.getCompanyName());

        TextView email = view.findViewById(R.id.email);
        email.setText(getBusinessDTO.getCompanyEmail());

        TextView address = view.findViewById(R.id.address);
        address.setText(getBusinessDTO.getAddress());

        TextView phone = view.findViewById(R.id.phone);
        phone.setText(getBusinessDTO.getPhone());

        TextView description = view.findViewById(R.id.description);
        description.setText(getBusinessDTO.getDescription());

        setMainImage(getBusinessDTO);

    }


    private void setMainImage(GetBusinessDTO getBusinessDTO) {
        ImageView mainImage = view.findViewById(R.id.mainImage);
        String imgUrl = getBusinessDTO.getMainImageUrl();

        String fullUrl = imgUrl.startsWith("http") ? imgUrl : "http://" + BuildConfig.IP_ADDR + ":8080" + imgUrl;

        Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.user_logo)
                .error(R.drawable.user_logo)
                .into(mainImage);
    }


    private void getCurrentBusiness() {
        String authorization = ClientUtils.getAuthorization(requireContext());

        Call<GetBusinessDTO> call = ClientUtils.businessService.getBusinessForCurrentUser(authorization);

        call.enqueue(new Callback<GetBusinessDTO>() {
            @Override
            public void onResponse(Call<GetBusinessDTO> call, Response<GetBusinessDTO> response) {
                if (response.isSuccessful()) {
                    GetBusinessDTO dto = response.body();

                    companyEmail = dto.getCompanyEmail();

                    if (companyEmail == null) {
                        Toast.makeText(requireActivity(), "You don't have an active " +
                                "business account!", Toast.LENGTH_SHORT).show();
                    }

                    setUpFormDetails(dto);
                }
                else if (response.code() == 204 || response.body() == null) {
                    Toast.makeText(requireActivity(), "You don't have an active " +
                            "business account!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<GetBusinessDTO> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed to load business information!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deactivate() {
        if (companyEmail == null) {
            Toast.makeText(requireActivity(), "You cannot deactivate inactive business!", Toast.LENGTH_SHORT).show();
        }
        else {
            String authorization = ClientUtils.getAuthorization(requireContext());

            Call<ResponseBody> call = ClientUtils.businessService.deactivateBusiness(authorization, companyEmail);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Deactivated business account!", Toast.LENGTH_SHORT).show();

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, new HomepageFragment())
                                .commit();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(requireActivity(), "Failed to deactivate business account!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void openBusinessEditFragment(View view) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new BusinessEditFragment())
                .commit();
    }


}