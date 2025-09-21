package com.example.eventplanner.fragments.report;

import com.example.eventplanner.dto.report.CreateReportDTO;
import com.example.eventplanner.dto.report.CreatedReportDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReportService {

    @POST("reports")
    Call<CreatedReportDTO> createReport(@Header("Authorization") String authorization,
                                        @Body CreateReportDTO createReportDTO);

}
