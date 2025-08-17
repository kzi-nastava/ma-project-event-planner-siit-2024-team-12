package com.example.eventplanner.adapters.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<String> eventTitles;

    public EventAdapter(List<String> eventTitles) {
        this.eventTitles = eventTitles;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String title = eventTitles.get(position);
        holder.title.setText(title);
        holder.description.setText("Description for " + title);
        holder.viewDetails.setOnClickListener(v -> {
            // Handle klik na "View Details"
        });
    }

    @Override
    public int getItemCount() {
        return eventTitles.size();
    }

    public void updateData(List<String> newTitles) {
        this.eventTitles = newTitles;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;
        Button viewDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            viewDetails = itemView.findViewById(R.id.viewDetailsButton);
        }
    }

}

