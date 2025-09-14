package com.example.eventplanner.adapters.solutionservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.service.ServiceEditActivity;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.utils.ClientUtils;
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
        
         Glide.with(holder.itemView.getContext())
             .load(ClientUtils.BASE_IMAGE_URL + service.getImageUrl())
             .placeholder(R.drawable.shopping_cart)
             .into(holder.serviceImage);

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