package com.example.eventplanner.fragments.servicereservation;

import com.example.eventplanner.dto.servicereservation.CreateServiceReservationDTO;
import com.example.eventplanner.dto.servicereservation.CreatedServiceReservationDTO;
import com.example.eventplanner.dto.servicereservation.GetServiceReservationDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceReservationService {

    @POST("service-reservations/{serviceId}/reserve")
    Call<CreatedServiceReservationDTO> reserveService(
            @Header("Authorization") String auth,
            @Path("serviceId") Long serviceId,
            @Body CreateServiceReservationDTO request
    );

    @PATCH("service-reservations/{reservationId}/cancel")
    Call<Map<String, String>> cancelReservation(
            @Header("Authorization") String auth,
            @Path("reservationId") Long reservationId
    );

    @GET("service-reservations/my-service-reservations")
    Call<List<GetServiceReservationDTO>> getReservationsForOrganizer(
            @Header("Authorization") String auth
    );

    @GET("service-reservations/{reservationId}")
    Call<GetServiceReservationDTO> getReservationDetails(
            @Header("Authorization") String auth,
            @Path("reservationId") Long reservationId
    );
}
