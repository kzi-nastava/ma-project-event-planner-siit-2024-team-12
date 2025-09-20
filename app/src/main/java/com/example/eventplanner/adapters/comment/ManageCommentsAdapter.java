package com.example.eventplanner.adapters.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.comment.GetCommentDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageCommentsAdapter extends RecyclerView.Adapter<ManageCommentsAdapter.CommentViewHolder> {

    private List<GetCommentDTO> commentList;
    private OnActionListener listener;

    public interface OnActionListener {
        void onApproveClick(Long commentId);
        void onDeleteClick(Long commentId);
        void onUserClick(String userEmail);
        void onViewDetailsClick(Long entityId, String entityType);
    }

    public ManageCommentsAdapter(List<GetCommentDTO> commentList, OnActionListener listener) {
        this.commentList = commentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        GetCommentDTO comment = commentList.get(position);
        holder.bind(comment, listener);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, timestamp, content, status, viewDetails;
        Button btnApprove, btnDelete;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            timestamp = itemView.findViewById(R.id.timestamp);
            content = itemView.findViewById(R.id.content);
            status = itemView.findViewById(R.id.tvStatus);
            viewDetails = itemView.findViewById(R.id.viewDetails);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(GetCommentDTO comment, OnActionListener listener) {
            userEmail.setText(comment.getUser());
            timestamp.setText(FORMATTER.format(comment.getCreatedAt()));
            content.setText(comment.getContent());
            status.setText("Status: " + comment.getStatus().name());

            String entityType = comment.getEntityType().toUpperCase();
            if ("SERVICE".equalsIgnoreCase(entityType)) {
                viewDetails.setText("View service details");
            } else if ("EVENT".equalsIgnoreCase(entityType)) {
                viewDetails.setText("View event details");
            } else if ("PRODUCT".equalsIgnoreCase(entityType)) {
                viewDetails.setText("View product details");
            }

            btnApprove.setOnClickListener(v -> listener.onApproveClick(comment.getId()));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(comment.getId()));
            userEmail.setOnClickListener(v -> listener.onUserClick(comment.getUser()));
            viewDetails.setOnClickListener(v -> listener.onViewDetailsClick(comment.getEntityId(), comment.getEntityType()));
        }
    }

    public void setCommentList(List<GetCommentDTO> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }
}
