package com.example.eventplanner.adapters.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.solution.FavSolutionDTO;

import java.util.List;

public class FavoriteServicesAdapter extends RecyclerView.Adapter<FavoriteServicesAdapter.ViewHolder> {
    public interface OnServiceClickListener {
        void onServiceClick(Long serviceId);
    }
    private List<FavSolutionDTO> services;
    private final OnServiceClickListener listener;

    public FavoriteServicesAdapter(List<FavSolutionDTO> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @Override
    public FavoriteServicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_service, parent, false);
        return new FavoriteServicesAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(FavoriteServicesAdapter.ViewHolder holder, int position) {
        FavSolutionDTO service = services.get(position);

        holder.serviceTitle.setText(service.getName());
        holder.serviceLocation.setText(service.getCity());
        holder.servicePrice.setText(service.getPrice().toString());
        holder.serviceCategory.setText(service.getCategoryName());


        if (position % 2 == 0) {
            holder.serviceImage.setImageResource(R.drawable.service1);
            holder.container.removeAllViews();
            holder.container.addView(holder.serviceImage);
            holder.container.addView(holder.textContainer);
        } else {
            holder.serviceImage.setImageResource(R.drawable.service3);
            holder.container.removeAllViews();
            holder.container.addView(holder.textContainer);
            holder.container.addView(holder.serviceImage);
        }

        holder.seeMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service.getId());
            }
        });
    }




    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceTitle, seeMore, serviceLocation, servicePrice, serviceCategory;
        LinearLayout container, textContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceTitle = itemView.findViewById(R.id.serviceTitle);
            seeMore = itemView.findViewById(R.id.seeMore);
            textContainer = itemView.findViewById(R.id.textContainer);
            serviceLocation = itemView.findViewById(R.id.serviceLocation);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceCategory = itemView.findViewById(R.id.serviceCategory);
        }
    }

}
