package com.example.eventplanner.adapters;

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

public class PSAdapter extends RecyclerView.Adapter<PSAdapter.PSViewHolder> {

    private List<String> eventTitles;

    public PSAdapter(List<String> eventTitles) {
        this.eventTitles = eventTitles;
    }

    @NonNull
    @Override
    public PSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_list, parent, false);
        return new PSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PSViewHolder holder, int position) {
        String title = eventTitles.get(position);
        holder.title.setText(title);
        holder.description.setText("Description for " + title);
        holder.itemView.setOnClickListener(v -> {

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

    static class PSViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;
        Button viewDetails;

        public PSViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            //viewDetails = itemView.findViewById(R.id.viewDetailsButton);
        }
    }

}

