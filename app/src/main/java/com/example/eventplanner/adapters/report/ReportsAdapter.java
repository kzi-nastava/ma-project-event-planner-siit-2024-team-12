package com.example.eventplanner.adapters.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.report.GetReportDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private List<GetReportDTO> reports;
    private OnItemClickListener listener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public interface OnItemClickListener {
        void onDeleteClick(Long reportId);
        void onSuspendClick(Long userId);
    }

    public ReportsAdapter(List<GetReportDTO> reports, OnItemClickListener listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        GetReportDTO report = reports.get(position);
        holder.reportedByEmail.setText(report.getReportedByEmail());
        holder.reportedUserEmail.setText(report.getReportedUserEmail());
        holder.reportReason.setText(report.getReason());
        holder.createdAt.setText(report.getCreatedAt().format(formatter));

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(report.getId());
            }
        });

        holder.btnSuspend.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuspendClick(report.getReportedUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void setReports(List<GetReportDTO> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportedByEmail, reportedUserEmail, reportReason, createdAt;
        Button btnDelete, btnSuspend;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportedByEmail = itemView.findViewById(R.id.reported_by_email);
            reportedUserEmail = itemView.findViewById(R.id.reported_user_email);
            reportReason = itemView.findViewById(R.id.report_reason);
            createdAt = itemView.findViewById(R.id.created_at);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnSuspend = itemView.findViewById(R.id.btn_suspend);
        }
    }
}