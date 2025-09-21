package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.report.ReportsAdapter;
import com.example.eventplanner.dto.report.GetReportDTO;

import java.util.List;

public class ReportViewHolder extends RecyclerView.ViewHolder {
    public TextView reportedByEmail, reportedUserEmail, reportReason, createdAt;
    public Button btnDelete, btnSuspend;

    public ReportViewHolder(@NonNull View itemView, final ReportsAdapter.OnItemClickListener listener, final List<GetReportDTO> reports) {
        super(itemView);
        reportedByEmail = itemView.findViewById(R.id.reported_by_email);
        reportedUserEmail = itemView.findViewById(R.id.reported_user_email);
        reportReason = itemView.findViewById(R.id.report_reason);
        createdAt = itemView.findViewById(R.id.created_at);
        btnDelete = itemView.findViewById(R.id.btn_delete);
        btnSuspend = itemView.findViewById(R.id.btn_suspend);

        btnDelete.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onDeleteClick(reports.get(position).getId());
            }
        });

        btnSuspend.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onSuspendClick(reports.get(position).getReportedUserId());
            }
        });

        reportedByEmail.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onUserClick(reports.get(position).getReportedByEmail());
            }
        });

        reportedUserEmail.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onUserClick(reports.get(position).getReportedUserEmail());
            }
        });
    }
}