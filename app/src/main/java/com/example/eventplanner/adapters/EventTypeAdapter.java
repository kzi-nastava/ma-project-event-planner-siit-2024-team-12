package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.EventType;
import com.example.eventplanner.activities.ViewEventTypeActivity;
import com.example.eventplanner.activities.EditEventTypeActivity;

import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeAdapter.EventTypeViewHolder> {
    private List<EventType> eventTypeList;

    public EventTypeAdapter(List<EventType> eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    @NonNull
    @Override
    public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_type_row, parent, false);
        return new EventTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
        EventType eventType = eventTypeList.get(position);

        holder.idTextView.setText(eventType.getId());
        holder.nameTextView.setText(eventType.getName());

        // Show or hide status and buttons based on expansion state
        if (eventType.isExpanded()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
            holder.viewButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.VISIBLE);
            holder.expandArrow.setRotation(180f);
        } else {
            holder.statusTextView.setVisibility(View.GONE);
            holder.viewButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
            holder.expandArrow.setRotation(0f);
        }

        // Alternate row background color
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.even_row_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.odd_row_color));
        }

        holder.itemView.setOnClickListener(v -> {
            eventType.setExpanded(!eventType.isExpanded());
            notifyItemChanged(position);
        });

        // Set click listeners for the buttons
        holder.viewButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ViewEventTypeActivity.class);
            context.startActivity(intent);
        });

        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditEventTypeActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventTypeList.size();
    }

    public static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView, nameTextView, statusTextView;
        ImageView expandArrow;
        Button viewButton, editButton;

        public EventTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.eventTypeId);
            nameTextView = itemView.findViewById(R.id.eventTypeName);
            statusTextView = itemView.findViewById(R.id.eventTypeStatus);
            expandArrow = itemView.findViewById(R.id.expandArrow);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
