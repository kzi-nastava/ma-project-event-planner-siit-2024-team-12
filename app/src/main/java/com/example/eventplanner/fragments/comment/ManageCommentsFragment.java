package com.example.eventplanner.fragments.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_comments, container, false);
        rvPendingComments = view.findViewById(R.id.rvPendingComments);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ManageCommentsAdapter(pendingComments, this);
        rvPendingComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingComments.setAdapter(adapter);

        fetchPendingComments();
    }

    private void fetchPendingComments() {
        String authHeader = ClientUtils.getAuthorization(getContext());

        ClientUtils.commentService.getPendingComments(authHeader, 0, 20).enqueue(new Callback<PageResponse<GetCommentDTO>>() {
            @Override
            public void onResponse(Call<PageResponse<GetCommentDTO>> call, Response<PageResponse<GetCommentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendingComments.clear();
                    pendingComments.addAll(response.body().getContent());
                    adapter.setCommentList(pendingComments);
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