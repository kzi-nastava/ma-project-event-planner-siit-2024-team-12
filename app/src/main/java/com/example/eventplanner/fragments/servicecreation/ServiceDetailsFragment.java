package com.example.eventplanner.fragments.servicecreation;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.homepage.HomepageActivity;
import com.example.eventplanner.dto.business.GetBusinessAndProviderDTO;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.fragments.conversation.ConversationFragment;
import com.example.eventplanner.fragments.profile.BusinessProviderDialogFragment;
import com.example.eventplanner.fragments.servicereservation.ServiceReservationDialogFragment;
import com.example.eventplanner.utils.ClientUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsFragment extends Fragment {

    private static final String ARG_SERVICE_ID = "service_id";
    private Long serviceId;

    private ImageView serviceImage, fav, favOutline, providerInfo;

    private Boolean isFavorite = false;
    private TextView serviceTitle;
    private EditText name, city, price, discount, availability, reservationDeadline, cancellationDeadline, description;
    private Button chatButton, bookServiceButton;
    private GetServiceDTO service;
    private TextView bookServiceText;

    public static ServiceDetailsFragment newInstance(Long serviceId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceId = getArguments().getLong(ARG_SERVICE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceImage = view.findViewById(R.id.serviceImage);
        serviceTitle = view.findViewById(R.id.serviceTitle);
        name = view.findViewById(R.id.name);
        city = view.findViewById(R.id.city);
        price = view.findViewById(R.id.price);
        discount = view.findViewById(R.id.discount);
        availability = view.findViewById(R.id.availability);
        reservationDeadline = view.findViewById(R.id.reservationDeadline);
        cancellationDeadline = view.findViewById(R.id.cancellationDeadline);
        description = view.findViewById(R.id.description);
        chatButton = view.findViewById(R.id.chatButton);
        bookServiceButton = view.findViewById(R.id.bookServiceButton);
        fav = view.findViewById(R.id.fav);
        favOutline = view.findViewById(R.id.favOutline);
        bookServiceText = view.findViewById(R.id.bookServiceText);
        bookServiceText.setText(HtmlCompat.fromHtml(
                getString(R.string.book_service_text),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        ));

        fetchServiceDetails();

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String role = prefs.getString("userRole", "");


        if (role.equals(UserRole.ROLE_ORGANIZER.toString())) {
            chatButton.setVisibility(View.VISIBLE);
        }else{
            chatButton.setVisibility(View.GONE);
        }

        chatButton.setOnClickListener(v -> {
            startChatWithProvider();
        });
        providerInfo = view.findViewById(R.id.providerInfo);
        providerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (serviceId == null) {
                    Toast.makeText(getContext(), "Service details not available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                BusinessProviderDialogFragment dialogFragment =
                        BusinessProviderDialogFragment.newInstance("service", serviceId);

                dialogFragment.show(getParentFragmentManager(), "BusinessProviderDialog");

            }
        });
    }

    private void startChatWithProvider() {

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String organizerEmail = prefs.getString("email", "");
        if (organizerEmail.isEmpty()) {
            Toast.makeText(getContext(), "Please log in to start a chat.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String type = "service";
        final Long solutionId = serviceId;
        final String auth = ClientUtils.getAuthorization(requireContext());

        if (auth.isEmpty() || solutionId == null) {
            Toast.makeText(getContext(), "Authentication or product ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Long> call = ClientUtils.conversationService.getConversationIdForSolutionOwner(
                auth,
                type,
                solutionId
        );

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {
                        Long conversationId = response.body();
                        openConversationFragment(conversationId);

                    } else if (response.code() == 204) {
                        Toast.makeText(getContext(), "Conversation not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ChatAPI", "Failed to get conversation ID. HTTP " + response.code());
                    String msg = "Error checking chat status: " + response.code();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("ChatAPI", "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: Failed to connect to chat service.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openConversationFragment(Long conversationId) {

        final String type = "service";
        final Long solutionId = serviceId;
        final String auth = ClientUtils.getAuthorization(requireContext());

        if (auth.isEmpty() || solutionId == null || conversationId == null) {
            Toast.makeText(getContext(), "Missing data to open chat.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<GetBusinessAndProviderDTO> call = ClientUtils.userService.getBusinessProviderDetails(
                auth,
                type,
                solutionId
        );

        call.enqueue(new Callback<GetBusinessAndProviderDTO>() {
            @Override
            public void onResponse(Call<GetBusinessAndProviderDTO> call, Response<GetBusinessAndProviderDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetBusinessAndProviderDTO dto = response.body();

                    String otherUserEmail = dto.getProviderEmail();
                    String name = dto.getProviderName() != null ? dto.getProviderName() : "";
                    String surname = dto.getProviderSurname() != null ? dto.getProviderSurname() : "";

                    String otherUserName = (name + " " + surname).trim();

                    if (otherUserEmail == null || otherUserEmail.isEmpty()) {
                        Toast.makeText(getContext(), "Provider email missing.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (otherUserName.isEmpty()) {
                        otherUserName = otherUserEmail;
                    }

                    ConversationFragment chatFragment = ConversationFragment.newInstance(
                            conversationId,
                            otherUserName,
                            otherUserEmail,
                            true
                    );

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.chat_fragment_container, chatFragment)
                            .addToBackStack(null)
                            .commit();
                    if (requireActivity() instanceof HomepageActivity) {
                        ((HomepageActivity) requireActivity()).openChatSidebar();
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load provider details: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBusinessAndProviderDTO> call, Throwable t) {
                Log.e("Chat", "Network error getting provider details: " + t.getMessage());
                Toast.makeText(getContext(), "Network error, cannot load chat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchServiceDetails() {
        if (serviceId != null) {
            Log.d("ServiceDetailsFragment", "Received service ID: " + serviceId);
        } else {
            Log.e("ServiceDetailsFragment", "Received service ID is NULL!");
        }

        String authorization = ClientUtils.getAuthorization(getContext());
        if (authorization == null) {
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.serviceSolutionService.getService(authorization, serviceId).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    service = response.body();
                    populateUI(response.body());
                    setUpFavService();
                    setupBookServiceButton();
                    SharedPreferences pref =  requireContext().getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    String role = pref.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());
                    if ("ROLE_ORGANIZER".equals(role)) {
                        bookServiceButton.setVisibility(View.VISIBLE);
                        bookServiceText.setVisibility(View.GONE);
                        setupBookServiceButton();
                    } else {
                        bookServiceButton.setVisibility(View.GONE);
                        bookServiceText.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (response.errorBody() != null) {
                        try {
                            String error = response.errorBody().string();
                            Log.e("ServiceDetailsFragment", "API Error: " + response.code() + " " + error);
                        } catch (IOException e) {
                            Log.e("ServiceDetailsFragment", "Error reading error body", e);
                        }
                    } else {
                        Log.e("ServiceDetailsFragment", "API Error: " + response.code() + " (No error body)");
                    }

                    Toast.makeText(getContext(), "Failed to fetch service details.", Toast.LENGTH_SHORT).show();
                    closeForm();
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                Log.e("ServiceDetailsFragment", "Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                closeForm();
            }
        });
    }

    private void populateUI(GetServiceDTO service) {
        serviceTitle.setText(service.getName());
        name.setText(service.getName());
        city.setText(service.getCity());
        price.setText(String.format("%s $", service.getPrice()));
        discount.setText(String.format("%d%%", service.getDiscount().intValue()));
        availability.setText(service.getAvailable() ? "Available" : "Unavailable");
        reservationDeadline.setText(String.format("%d days", service.getReservationDeadline()));
        cancellationDeadline.setText(String.format("%d days", service.getCancellationDeadline()));
        description.setText(service.getDescription());

        loadServiceImage(service.getImageUrl());
    }

    private void loadServiceImage(String imageUrl) {
        if (getContext() != null && isAdded() && imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl = "http://" + BuildConfig.IP_ADDR + ":8080" + imageUrl;

            Glide.with(getContext())
                    .load(fullUrl)
                    .centerCrop()
                    .placeholder(R.drawable.service1)
                    .error(R.drawable.service1)
                    .into(serviceImage);
        }
    }

    private void setUpFavService() {
        checkIfFavorite();

        favOutline.setOnClickListener(v -> {
            addToFavorites();
        });

        fav.setOnClickListener(v -> {
            removeFromFavorites();
        });
    }

    private void checkIfFavorite() {
        if (serviceId == null || getContext() == null) {
            return;
        }
        isFavorite = false;
        String auth = ClientUtils.getAuthorization(getContext());

        SharedPreferences pref = getContext().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String email = pref.getString("email", "a");

        Call<Boolean> call = ClientUtils.userService.isServiceFavorite(auth, email, serviceId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = response.body();
                    if (isFavorite) {
                        fav.setVisibility(View.VISIBLE);
                        favOutline.setVisibility(View.GONE);
                    } else {
                        favOutline.setVisibility(View.VISIBLE);
                        fav.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to check if favorite!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFavorites() {
        if (serviceId == null || getContext() == null) {
            return;
        }
        String auth = ClientUtils.getAuthorization(getContext());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "a");
        String role = sharedPreferences.getString("userRole", "");

        if(auth.isEmpty()){
            Toast.makeText(requireActivity(), "Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(role.equals(UserRole.ROLE_AUTHENTICATED_USER.toString())){
            Toast.makeText(requireActivity(), "Upgrade your role first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(role.equals(UserRole.ROLE_ADMIN.toString())){
            Toast.makeText(requireActivity(), "You are not allowed to add to favorites.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = ClientUtils.userService.addFavoriteService(auth, userEmail, serviceId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.VISIBLE);
                    favOutline.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Added service to favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to add service to favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromFavorites() {
        if (serviceId == null || getContext() == null) {
            return;
        }
        String auth = ClientUtils.getAuthorization(getContext());
        SharedPreferences pref = getContext().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String email = pref.getString("email", "a");
        String role = pref.getString("userRole", "");

        if(auth.isEmpty()){
            Toast.makeText(requireActivity(), "Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(role.equals(UserRole.ROLE_AUTHENTICATED_USER.toString())){
            Toast.makeText(requireActivity(), "Upgrade your role first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(role.equals(UserRole.ROLE_ADMIN.toString())){
            Toast.makeText(requireActivity(), "You are not allowed to remove from favorites.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = ClientUtils.userService.removeFavoriteService(auth, email, serviceId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fav.setVisibility(View.GONE);
                    favOutline.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Removed service from favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to remove service from favorites!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeForm() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }


    private void setupBookServiceButton() {
        bookServiceButton.setOnClickListener(v -> {
            if (service != null) {

                ServiceReservationDialogFragment dialog = new ServiceReservationDialogFragment();

                Bundle args = new Bundle();
                args.putLong("SERVICE_ID", serviceId);
                args.putLong("FIXED_DURATION", service.getFixedTime());
                args.putLong("MIN_DURATION", service.getMinTimeMinutes());
                args.putLong("MAX_DURATION", service.getMaxTimeMinutes());

                dialog.setArguments(args);

                dialog.show(getParentFragmentManager(), "ServiceReservationDialog");
            } else {
                Toast.makeText(getContext(), "Service details not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

}