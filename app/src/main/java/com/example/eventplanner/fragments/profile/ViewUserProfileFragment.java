package com.example.eventplanner.fragments.profile;

import android.content.Intent;
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
import com.example.eventplanner.R;
import com.example.eventplanner.dto.user.GetUserDTO;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.fragments.report.ReportUserFragment;
import com.example.eventplanner.utils.ClientUtils;

import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewUserProfileFragment extends Fragment {

    private static final String ARG_USER_EMAIL = "user_email";
    private String userEmail;
    private Long reportedUserId;
    private GetUserDTO viewedUser;
    private Long currentUserId;
    private UserRole currentUserRole;

    private Set<Long> currentUserBlockedUsersIds;

    private TextView name, surname, email, phone, address;
    private ImageView mainImage, closeButton;
    private Button btnReportUser, btnBlockUser;

    public static ViewUserProfileFragment newInstance(String userEmail) {
        ViewUserProfileFragment fragment = new ViewUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_EMAIL, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_USER_EMAIL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_user_profile, container, false);

        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        mainImage = view.findViewById(R.id.mainImage);
        closeButton = view.findViewById(R.id.close_form);
        btnReportUser = view.findViewById(R.id.btnReportUser);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);
        btnBlockUser.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUserProfileAndCurrentUser();

        closeButton.setOnClickListener(v -> closeForm());
        btnReportUser.setOnClickListener(v -> reportUser());
        btnBlockUser.setOnClickListener(v -> {
            if (btnBlockUser.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) {
                showUnblockConfirmationDialog();
            } else {
                showBlockConfirmationDialog();
            }
        });
    }
    private void fetchUserProfileAndCurrentUser() {
        String authorization = ClientUtils.getAuthorization(getContext());
        ClientUtils.userService.getUserProfile(authorization, userEmail).enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewedUser = response.body();
                    setUpFormDetails(viewedUser);
                    reportedUserId = viewedUser.getId();

                    fetchCurrentUserProfile(authorization);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    closeForm();
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                closeForm();
            }
        });
    }

    private void fetchCurrentUserProfile(String authorization) {
        ClientUtils.authService.getCurrentUser(authorization).enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetUserDTO currentUser = response.body();
                    currentUserId = currentUser.getId();
                    currentUserRole = UserRole.valueOf(currentUser.getRole());
                    currentUserBlockedUsersIds = currentUser.getBlockedUsersIds();

                    updateBlockButtonState();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch current user data.", Toast.LENGTH_SHORT).show();
                    btnBlockUser.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error while fetching current user.", Toast.LENGTH_SHORT).show();
                btnBlockUser.setVisibility(View.GONE);
            }
        });
    }

    private void updateBlockButtonState() {
        if (viewedUser == null || currentUserRole == null || currentUserId == null || currentUserBlockedUsersIds == null) {
            btnBlockUser.setVisibility(View.GONE);
            btnReportUser.setVisibility(View.GONE);
            return;
        }

        if (Objects.equals(currentUserId, viewedUser.getId())) {
            btnReportUser.setVisibility(View.GONE);
        } else {
            btnReportUser.setVisibility(View.VISIBLE);
        }

        boolean canBlock = canBlockUser(currentUserRole.toString(), viewedUser.getRole());

        if (Objects.equals(currentUserId, viewedUser.getId())) {
            canBlock = false;
        }

        if (canBlock) {
            btnBlockUser.setVisibility(View.VISIBLE);
            if (currentUserBlockedUsersIds.contains(viewedUser.getId())) {
                btnBlockUser.setText(R.string.unblock_user);
            } else {
                btnBlockUser.setText(R.string.block_user);
            }
        } else {
            btnBlockUser.setVisibility(View.GONE);
        }
    }


    private void setUpFormDetails(GetUserDTO user) {
        name.setText(user.getName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        if (user.getLocation() != null) {
            String fullAddress = user.getLocation().getAddress() + ", " + user.getLocation().getCity() + ", " + user.getLocation().getCountry();
            address.setText(fullAddress);
        }
        setMainImage(user.getImageUrl());
    }

    private void setMainImage(String imageUrl) {
        if (getContext() != null) {
            String fullUrl = "http://10.0.2.2:8080" + imageUrl;
            Glide.with(getContext())
                    .load(fullUrl)
                    .placeholder(R.drawable.user_logo)
                    .error(R.drawable.user_logo)
                    .into(mainImage);
        }
    }

    private void closeForm() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void reportUser() {
        if (reportedUserId != null) {
            ReportUserFragment reportDialog = ReportUserFragment.newInstance(reportedUserId);
            reportDialog.show(getParentFragmentManager(), "ReportUserFragment");
        } else {
            Toast.makeText(getContext(), "User data not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    private void blockUser() {
        String authorization = ClientUtils.getAuthorization(getContext());
        if (viewedUser == null) return;

        ClientUtils.userService.blockUser(authorization, viewedUser.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User successfully blocked!", Toast.LENGTH_SHORT).show();
                    fetchUserProfileAndCurrentUser();
                } else {
                    Toast.makeText(getContext(), "Failed to block user.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unblockUser() {
        String authorization = ClientUtils.getAuthorization(getContext());
        if (viewedUser == null) return;

        ClientUtils.userService.unblockUser(authorization, viewedUser.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User successfully unblocked!", Toast.LENGTH_SHORT).show();
                    fetchUserProfileAndCurrentUser();
                } else {
                    Toast.makeText(getContext(), "Failed to unblock user.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean canBlockUser(String blockerRole, String blockedRole) {
        if (blockerRole == null || blockedRole == null) {
            return false;
        }

        UserRole blockerRoleEnum = UserRole.valueOf(blockerRole);
        UserRole blockedRoleEnum = UserRole.valueOf(blockedRole);

        if (blockerRoleEnum == UserRole.ROLE_ORGANIZER && blockedRoleEnum == UserRole.ROLE_PROVIDER) {
            return true;
        }
        if (blockerRoleEnum == UserRole.ROLE_PROVIDER && blockedRoleEnum == UserRole.ROLE_ORGANIZER) {
            return true;
        }
        if (blockerRoleEnum == UserRole.ROLE_AUTHENTICATED_USER && blockedRoleEnum == UserRole.ROLE_ORGANIZER) {
            return true;
        }
        if (blockerRoleEnum == UserRole.ROLE_ORGANIZER && blockedRoleEnum == UserRole.ROLE_AUTHENTICATED_USER) {
            return true;
        }
        return false;
    }

    private void showBlockConfirmationDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext(), R.style.RoundedAlertDialogTheme)
                .setTitle("Block User")
                .setMessage("Are you sure you want to block this user? \nThis will prevent them from contacting you and interacting with your content.")
                .setPositiveButton("Block", (dialog, which) -> {
                    blockUser();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showUnblockConfirmationDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext(), R.style.RoundedAlertDialogTheme)
                .setTitle("Unblock User")
                .setMessage("Are you sure you want to unblock this user? \nThey will be able to contact you again and interact with your content.")
                .setPositiveButton("Unblock", (dialog, which) -> {
                    unblockUser();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}