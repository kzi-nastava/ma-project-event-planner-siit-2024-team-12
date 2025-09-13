package com.example.eventplanner.adapters.solutionservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.service.ServiceEditActivity;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ServiceCardAdapter extends RecyclerView.Adapter<ServiceCardAdapter.ServiceCardViewHolder> {

    private List<GetServiceDTO> services;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDetailsClick(GetServiceDTO service);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ServiceCardAdapter(List<GetServiceDTO> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card_item, parent, false);
        return new ServiceCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceCardViewHolder holder, int position) {
        GetServiceDTO service = services.get(position);
        holder.serviceTitle.setText(service.getName());

        // Ovdje postavite sliku za servis
        // Preporučuje se upotreba biblioteka poput Glide ili Picasso za efikasno učitavanje slika s URL-a.
        // Primjer:
        // Glide.with(holder.itemView.getContext())
        //     .load(service.getImageUrl())
        //     .placeholder(R.drawable.placeholder_image)
        //     .into(holder.serviceImage);

        holder.detailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailsClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services != null ? services.size() : 0;
    }

    // Metoda za ažuriranje liste servisa
    public void updateServices(List<GetServiceDTO> newServices) {
        this.services = newServices;
        notifyDataSetChanged();
    }

    static class ServiceCardViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView serviceImage;
        TextView serviceTitle;
        AppCompatButton detailsButton;

        public ServiceCardViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            serviceTitle = itemView.findViewById(R.id.service_title);
            detailsButton = itemView.findViewById(R.id.service_details_button);
        }
    }
}