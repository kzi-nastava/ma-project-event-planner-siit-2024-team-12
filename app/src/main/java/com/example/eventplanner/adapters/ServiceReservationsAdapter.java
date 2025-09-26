package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.servicereservation.GetServiceReservationDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServiceReservationsAdapter extends RecyclerView.Adapter<ServiceReservationsAdapter.ResViewHolder> {

    private List<GetServiceReservationDTO> reservations;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GetServiceReservationDTO reservation);
    }

    public ServiceReservationsAdapter(List<GetServiceReservationDTO> reservations, OnItemClickListener listener) {
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_reservation, parent, false);
        return new ResViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResViewHolder holder, int position) {
        GetServiceReservationDTO r = reservations.get(position);

        String dateToDisplay = r.getServiceLocalDate() != null
                ? r.getServiceLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : (r.getEventLocalDate() != null
                ? r.getEventLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : "");


        holder.tvEventName.setText(r.getEventName());
        holder.tvServiceAndDate.setText(r.getServiceName() + " • " + dateToDisplay);
        holder.tvStatus.setText(r.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(r));
    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }


    static class ResViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvServiceAndDate, tvStatus;
        ImageView ivChevron;
        ResViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvServiceAndDate = itemView.findViewById(R.id.tvServiceAndDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivChevron = itemView.findViewById(R.id.ivChevron);
        }
    }
}

