package com.example.eventplanner.fragments.report;


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
import com.example.eventplanner.adapters.report.ReportsAdapter;
import com.example.eventplanner.dto.report.GetReportDTO;
import com.example.eventplanner.utils.ClientUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportManagementFragment extends Fragment implements ReportsAdapter.OnItemClickListener {

    private RecyclerView reportsRecyclerView;
    private ReportsAdapter reportsAdapter;
    private List<GetReportDTO> reports = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_management, container, false);
        reportsRecyclerView = view.findViewById(R.id.reports_recycler_view);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportsAdapter = new ReportsAdapter(reports, this);
        reportsRecyclerView.setAdapter(reportsAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchReports();
    }

    private void fetchReports() {
        String authHeader = ClientUtils.getAuthorization(getContext());
        if (authHeader == null) {
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.reportService.getAllReports(authHeader, 0, 50).enqueue(new Callback<GetReportDTO>() {
            @Override
            public void onResponse(Call<GetReportDTO> call, Response<GetReportDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reportsAdapter.setReports((List<GetReportDTO>) response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to fetch reports.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetReportDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Long reportId) {
        String authHeader = ClientUtils.getAuthorization(getContext());
        ClientUtils.reportService.deleteReport(authHeader, reportId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Report deleted.", Toast.LENGTH_SHORT).show();
                    fetchReports();
                } else {
                    Toast.makeText(getContext(), "Failed to delete report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuspendClick(Long userId) {
        Toast.makeText(getContext(), "Suspend user with ID: " + userId, Toast.LENGTH_SHORT).show();
    }
}
