package com.example.eventplanner.adapters.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.ReportViewHolder;
import com.example.eventplanner.dto.report.GetReportDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportViewHolder> {

    private List<GetReportDTO> reports;
    private OnItemClickListener listener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public interface OnItemClickListener {
        void onDeleteClick(Long reportId);
        void onSuspendClick(Long userId);
        void onUserClick(String userEmail);
    }

    public ReportsAdapter(List<GetReportDTO> reports, OnItemClickListener listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view, listener, reports);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        GetReportDTO report = reports.get(position);
        holder.reportedByEmail.setText(report.getReportedByEmail());
        holder.reportedUserEmail.setText(report.getReportedUserEmail());
        holder.reportReason.setText(report.getReason());
        holder.createdAt.setText(report.getCreatedAt().format(formatter));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void setReports(List<GetReportDTO> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }
}