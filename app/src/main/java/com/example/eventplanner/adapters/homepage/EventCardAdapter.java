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
import com.example.eventplanner.dto.event.GetEventDTO;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.EventViewHolder> {

    private final Context context;
    private final List<GetEventDTO> items = new ArrayList<>();
    public static final String IP_ADDR = "192.168.0.28";

    public EventCardAdapter(Context context) { this.context = context; }

    public void setItems(List<GetEventDTO> data) {
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
        GetEventDTO e = items.get(position);

        holder.title.setText(e.getName() != null ? e.getName() : "");

        String img = e.getImageUrl();
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


        View.OnClickListener open = v -> {
            Intent i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("id", e.getId());
            context.startActivity(i);
        };
        holder.itemView.setOnClickListener(open);
        holder.viewDetails.setOnClickListener(open);
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
