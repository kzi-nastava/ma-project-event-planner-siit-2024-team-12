package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.EventTypeViewHolder;
import com.example.eventplanner.model.EventType;
import com.example.eventplanner.activities.eventtype.EventTypeViewActivity;
import com.example.eventplanner.activities.eventtype.EventTypeEditActivity;

import java.util.ArrayList;
import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeViewHolder> {
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

            holder.statusTextView.setText(eventType.getActive() ? "Active" : "Inactive");

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
            Intent intent = new Intent(context, EventTypeViewActivity.class);

            // Pass event type details to EventTypeViewActivity
            intent.putExtra("eventTypeName", eventType.getName());
            intent.putExtra("eventTypeDescription", eventType.getDescription());
            intent.putExtra("suggestedCategoryNames", new ArrayList<>(eventType.getSuggestedCategoryNames()));

            context.startActivity(intent);
        });


        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EventTypeEditActivity.class);

            intent.putExtra("eventTypeId", eventType.getId());
            intent.putExtra("eventTypeName", eventType.getName());
            intent.putExtra("eventTypeDescription", eventType.getDescription());
            intent.putExtra("suggestedCategoryNames", new ArrayList<>(eventType.getSuggestedCategoryNames()));
            intent.putExtra("isActive", eventType.getActive());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventTypeList.size();
    }

}
