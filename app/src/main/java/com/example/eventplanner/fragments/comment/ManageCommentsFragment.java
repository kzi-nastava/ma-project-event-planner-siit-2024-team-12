package com.example.eventplanner.fragments.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.comment.ManageCommentsAdapter;
import com.example.eventplanner.dto.PageResponse;
import com.example.eventplanner.dto.comment.GetCommentDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCommentsFragment extends Fragment implements ManageCommentsAdapter.OnActionListener {

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

        ClientUtils.commentService.getPendingComments(authHeader, currentPage, 4).enqueue(new Callback<PageResponse<GetCommentDTO>>() {
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

    @Override
    public void onApproveClick(Long commentId) {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.approveComment(authHeader, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Comment approved successfully", Toast.LENGTH_SHORT).show();
                    fetchPendingComments();
                } else {
                    Toast.makeText(getContext(), "Failed to approve comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Long commentId) {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.deleteComment(authHeader, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchPendingComments();
                } else {
                    Toast.makeText(getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserClick(String userEmail) {
        Toast.makeText(getContext(), "Navigation to user profile: " + userEmail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetailsClick(Long entityId, String entityType) {
        Toast.makeText(getContext(), "Navigating to " + entityType + " with ID: " + entityId, Toast.LENGTH_SHORT).show();

    }
}