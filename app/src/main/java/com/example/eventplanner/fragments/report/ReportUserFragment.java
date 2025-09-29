package com.example.eventplanner.fragments.report;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.report.CreateReportDTO;
import com.example.eventplanner.dto.report.CreatedReportDTO;
import com.example.eventplanner.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportUserFragment extends DialogFragment {

    private static final String ARG_REPORTED_USER_ID = "reported_user_id";

    private Long reportedUserId;
    private EditText etReason;
    private Button btnCancel;
    private Button btnReport;

    private ImageView closeDialog;

    public static ReportUserFragment newInstance(Long reportedUserId) {
        ReportUserFragment fragment = new ReportUserFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_REPORTED_USER_ID, reportedUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportedUserId = getArguments().getLong(ARG_REPORTED_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etReason = view.findViewById(R.id.etReason);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnReport = view.findViewById(R.id.btnReport);
        closeDialog = view.findViewById(R.id.close_dialog);

        btnCancel.setOnClickListener(v -> dismiss());
        closeDialog.setOnClickListener(v -> dismiss());

        btnReport.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(getContext(), "Reason cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                showConfirmationDialog(reason);
            }
        });
    }

    private void showConfirmationDialog(String reason) {
        new AlertDialog.Builder(getContext(), R.style.RoundedAlertDialogTheme)
                .setTitle("Confirm Report")
                .setMessage("Are you sure you want to report this user?")
                .setPositiveButton("Report", (dialog, which) -> reportUser(reason))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void reportUser(String reason) {
        String authHeader = ClientUtils.getAuthorization(getContext());

        CreateReportDTO createReportDTO = new CreateReportDTO();
        createReportDTO.setReportedUserId(reportedUserId);
        createReportDTO.setReason(reason);

        ClientUtils.reportService.createReport(authHeader, createReportDTO).enqueue(new Callback<CreatedReportDTO>() {
            @Override
            public void onResponse(Call<CreatedReportDTO> call, Response<CreatedReportDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User reported successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to report user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatedReportDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }
}