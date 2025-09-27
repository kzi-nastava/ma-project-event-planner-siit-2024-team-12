package com.example.eventplanner.fragments.comment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.activities.product.ProductDetailsActivity;
import com.example.eventplanner.adapters.comment.ManageCommentsAdapter;
import com.example.eventplanner.dto.PageResponse;
import com.example.eventplanner.dto.comment.GetCommentDTO;
import com.example.eventplanner.fragments.profile.ViewUserProfileFragment;
import com.example.eventplanner.fragments.servicecreation.ServiceDetailsFragment;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentManagementFragment extends Fragment implements ManageCommentsAdapter.OnActionListener {

    private RecyclerView rvPendingComments;
    private ManageCommentsAdapter adapter;
    private List<GetCommentDTO> pendingComments = new ArrayList<>();

    private ImageButton prevPageButton;
    private ImageButton nextPageButton;
    private TextView pageNumber;

    private int currentPage = 0;
    private int totalPages = 0;
    private int pageSize = 5;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPendingComments = view.findViewById(R.id.rvPendingComments);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        pageNumber = view.findViewById(R.id.pageNumber);

        adapter = new ManageCommentsAdapter(pendingComments, this);
        rvPendingComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingComments.setAdapter(adapter);

        prevPageButton.setOnClickListener(v -> navigateToPreviousPage());
        nextPageButton.setOnClickListener(v -> navigateToNextPage());

        fetchPendingComments();
    }

    private void fetchPendingComments() {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.getPendingComments(authHeader, currentPage, 5).enqueue(new Callback<PageResponse<GetCommentDTO>>() {
            @Override
            public void onResponse(Call<PageResponse<GetCommentDTO>> call, Response<PageResponse<GetCommentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<GetCommentDTO> pageResponse = response.body();
                    pendingComments.clear();
                    pendingComments.addAll(response.body().getContent());
                    adapter.setCommentList(pendingComments);

                    currentPage = pageResponse.getNumber();
                    totalPages = pageResponse.getTotalPages();
                    updatePaginationUI();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<GetCommentDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePaginationUI() {
        pageNumber.setText(String.format("%d/%d", currentPage + 1, totalPages));

        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);

        prevPageButton.setColorFilter(currentPage > 0 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.dark_gray));
        nextPageButton.setColorFilter(currentPage < totalPages - 1 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.dark_gray));
    }

    private void navigateToNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchPendingComments();
        }
    }

    private void navigateToPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            fetchPendingComments();
        }
    }

    private void showConfirmationDialog(Long commentId, String action) {
        if (getContext() == null) return;

        String title;
        String message;

        if (action.equals("approve")) {
            title = "Confirm approval";
            message = "Are you sure you want to approve this comment? Once approved, the comment will become public and visible to all users.";
        } else { // "delete"
            title = "Confirm deletion";
            message = "Are you sure you want to delete this comment? This action is permanent and the comment will be removed.";
        }

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if (action.equals("approve")) {
                        performApproveAction(commentId);
                    } else {
                        performDeleteAction(commentId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performApproveAction(Long commentId) {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.approveComment(authHeader, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Comment successfully approved!", Toast.LENGTH_SHORT).show();
                    fetchPendingComments();
                } else {
                    Toast.makeText(getContext(), "Failed to approve comment.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performDeleteAction(Long commentId) {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.deleteComment(authHeader, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Comment successfully deleted!", Toast.LENGTH_SHORT).show();
                    fetchPendingComments();
                } else {
                    Toast.makeText(getContext(), "Failed to delete comment.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApproveClick(Long commentId) {
        showConfirmationDialog(commentId, "approve");
    }

    @Override
    public void onDeleteClick(Long commentId) {
        showConfirmationDialog(commentId, "delete");
    }

    @Override
    public void onUserClick(String userEmail) {
        if (getParentFragmentManager() != null) {
            ViewUserProfileFragment userProfileFragment = ViewUserProfileFragment.newInstance(userEmail);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, userProfileFragment)
                  .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onViewDetailsClick(Long entityId, String entityType) {
        navigateToEntityDetails(entityId, entityType);
    }

    private void navigateToEntityDetails(Long entityId, String entityType) {
        if (getContext() == null) {
            return;
        }

        Intent intent;
        switch (entityType.toUpperCase()) {
            case "EVENT":
                intent = new Intent(getContext(), EventDetailsActivity.class);
                intent.putExtra("id", entityId);
                startActivity(intent);
                break;
            case "PRODUCT":
                intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("id", entityId);
                startActivity(intent);
                break;
            case "SERVICE":
                if (getContext() instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) getContext();

                    FrameLayout suspendedContainer = activity.findViewById(R.id.main_fragment_container);
                    if (suspendedContainer != null) {
                        suspendedContainer.setVisibility(View.VISIBLE);
                    }

                    ServiceDetailsFragment fragment = ServiceDetailsFragment.newInstance(entityId);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            default:
                Toast.makeText(getContext(), "Unknown entity type: " + entityType, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}