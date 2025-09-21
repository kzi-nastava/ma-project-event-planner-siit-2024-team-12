package com.example.eventplanner.fragments.report;

import com.example.eventplanner.dto.PageResponse;
import com.example.eventplanner.dto.report.CreateReportDTO;
import com.example.eventplanner.dto.report.CreatedReportDTO;
import com.example.eventplanner.dto.report.GetReportDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {

    @POST("reports")
    Call<CreatedReportDTO> createReport(@Header("Authorization") String authorization,
                                        @Body CreateReportDTO createReportDTO);

    @GET("reports")
    Call<PageResponse<GetReportDTO>> getAllReports(@Header("Authorization") String authorization,
                                                   @Query("page") int page,
                                                   @Query("size") int size);

    @DELETE("reports/reports/{reportId}")
    Call<ResponseBody> deleteReport(@Header("Authorization") String authorization, @Path("reportId") Long reportId);

}
