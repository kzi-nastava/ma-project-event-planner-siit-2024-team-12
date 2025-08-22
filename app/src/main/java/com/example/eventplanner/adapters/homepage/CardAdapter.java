package com.example.eventplanner.adapters.homepage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.event.EventDetailsActivity;
import com.example.eventplanner.activities.homepage.CardItem;
import com.example.eventplanner.activities.product.ProductDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.EventViewHolder> {

    private final Context context;
    private final List<CardItem> items = new ArrayList<>();
    public static final String IP_ADDR = "192.168.0.28";

    public CardAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<? extends CardItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CardItem item = items.get(position);

        holder.title.setText(item.getName() != null ? item.getName() : "");

        String img = item.getImageUrl();
        if (img != null && !img.isEmpty()) {
            img = "http://" + IP_ADDR + ":8080" + img;

            Glide.with(context)
                    .load(img)
                    .centerCrop()
                    .placeholder(R.drawable.event1)
                    .error(R.drawable.event1)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.event1);
        }

        holder.itemView.setOnClickListener(v -> openDetails(item));
        holder.viewDetails.setOnClickListener(v -> openDetails(item));
    }

    private void openDetails(CardItem item) {
        Intent i = null;

        if (item instanceof com.example.eventplanner.dto.event.GetEventDTO) {
            i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("id", item.getId());

        } else if (item instanceof com.example.eventplanner.dto.solution.GetHomepageSolutionDTO) {
            com.example.eventplanner.dto.solution.GetHomepageSolutionDTO solution =
                    (com.example.eventplanner.dto.solution.GetHomepageSolutionDTO) item;

            if ("product".equalsIgnoreCase(solution.getType())) {
                i = new Intent(context, ProductDetailsActivity.class);
            } 

            if (i != null) {
                i.putExtra("id", solution.getId());
            }
        }

        if (i != null) {
            context.startActivity(i);
        }
    }


    @Override
    public int getItemCount() { return items.size(); }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView title;
        TextView viewDetails;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.event_image);
            title = itemView.findViewById(R.id.event_name);
            viewDetails = itemView.findViewById(R.id.view_details_button);
        }
    }
}
