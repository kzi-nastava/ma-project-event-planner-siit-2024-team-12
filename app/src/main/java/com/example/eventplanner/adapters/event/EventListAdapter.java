package com.example.eventplanner.adapters.event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BuildConfig;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.dto.event.GetEventDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private final Context context;
    private List<GetEventDTO> events;

    public EventListAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
    }

    public void updateData(List<GetEventDTO> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        GetEventDTO event = events.get(position);
        holder.title.setText(event.getName() != null ? event.getName() : "");
        holder.description.setText(event.getDescription() != null ? event.getDescription() : "");
        holder.category.setText(event.getEventTypeName() != null ? event.getEventTypeName() : "");

        if (event.getDate() != null) {
            holder.date.setText(formatDate(event.getDate()));
        } else {
            holder.date.setText("");
        }

        if (event.getLocation() != null) {
            String locText = "";
            if (event.getLocation().getCity() != null) {
                locText += event.getLocation().getCity();
            }
            if (event.getLocation().getCountry() != null) {
                if (!locText.isEmpty()) locText += ", ";
                locText += event.getLocation().getCountry();
            }
            holder.location.setText(locText);
        } else {
            holder.location.setText("");
        }


        String img = event.getImageUrl();
        if (img != null && !img.isEmpty()) {
            img = "http://" + BuildConfig.IP_ADDR + ":8080" + img;
            Glide.with(context)
                    .load(img)
                    .centerCrop()
                    .placeholder(R.drawable.event1)
                    .error(R.drawable.event1)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.event1);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsFragment.class);
            intent.putExtra("id", event.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, location, date, category;
       // Button viewDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            location = itemView.findViewById(R.id.location);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);

            //viewDetails = itemView.findViewById(R.id.viewDetailsButton);
        }
    }
}
