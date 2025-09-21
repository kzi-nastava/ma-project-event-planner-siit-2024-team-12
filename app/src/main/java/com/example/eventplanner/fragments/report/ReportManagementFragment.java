package com.example.eventplanner.fragments.report;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import com.example.eventplanner.adapters.report.ReportsAdapter;
import com.example.eventplanner.dto.PageResponse;
import com.example.eventplanner.dto.report.GetReportDTO;
import com.example.eventplanner.fragments.profile.ViewUserProfileFragment;
import com.example.eventplanner.utils.ClientUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportManagementFragment extends Fragment implements ReportsAdapter.OnItemClickListener {

    private RecyclerView reportsRecyclerView;
    private ReportsAdapter reportsAdapter;
    private List<GetReportDTO> reports = new ArrayList<>();
    private TextView pageNumberTextView;
    private ImageButton prevPageButton, nextPageButton;
    private int currentPage = 0;
    private int totalPages = 0;
    private static final int PAGE_SIZE = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_management, container, false);

        reportsRecyclerView = view.findViewById(R.id.reports_recycler_view);
        pageNumberTextView = view.findViewById(R.id.pageNumber);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);

        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportsAdapter = new ReportsAdapter(reports, this);
        reportsRecyclerView.setAdapter(reportsAdapter);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                fetchReports(currentPage);
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fetchReports(currentPage);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchReports(currentPage);
    }

    private void fetchReports(int page) {
        String authHeader = ClientUtils.getAuthorization(getContext());
        if (authHeader == null) {
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClientUtils.reportService.getAllReports(authHeader, page, PAGE_SIZE).enqueue(new Callback<PageResponse<GetReportDTO>>() {
            @Override
            public void onResponse(Call<PageResponse<GetReportDTO>> call, Response<PageResponse<GetReportDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<GetReportDTO> pageResponse = response.body();
                    reportsAdapter.setReports(pageResponse.getContent());
                    currentPage = pageResponse.getNumber();
                    totalPages = pageResponse.getTotalPages();
                    updatePaginationUI();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch reports.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<GetReportDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePaginationUI() {
        pageNumberTextView.setText(String.format("%d/%d", currentPage + 1, totalPages));

        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);

        prevPageButton.setColorFilter(currentPage > 0 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.dark_gray));
        nextPageButton.setColorFilter(currentPage < totalPages - 1 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.dark_gray));
    }

    @Override
    public void onDeleteClick(Long reportId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to permanently delete this report? \nThis action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReport(reportId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReport(Long reportId) {
        String authHeader = ClientUtils.getAuthorization(getContext());
        ClientUtils.reportService.deleteReport(authHeader, reportId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Report successfully deleted!", Toast.LENGTH_SHORT).show();
                    fetchReports(currentPage);
                } else {
                    Toast.makeText(getContext(), "Failed to delete report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuspendClick(Long userId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Suspension")
                .setMessage("Are you sure you want to suspend this user? \nThe user will be suspended for 3 days. This action cannot be undone.")
                .setPositiveButton("Suspend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        suspendUser(userId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void suspendUser(Long userId) {
        String authHeader = ClientUtils.getAuthorization(getContext());
        ClientUtils.userService.suspendUser(authHeader, userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User successfully suspended for 3 days!", Toast.LENGTH_SHORT).show();
                    fetchReports(currentPage);
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error.";
                        Log.e("SuspendUser", "Error: " + errorMessage);
                        Toast.makeText(getContext(), "Failed to suspend user: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to suspend user.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SuspendUser", "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserClick(String userEmail) {
        if (getParentFragmentManager() != null) {
            ViewUserProfileFragment userProfileFragment = ViewUserProfileFragment.newInstance(userEmail);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.notifications_container, userProfileFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}