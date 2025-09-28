package com.example.eventplanner.adapters.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.viewholders.EventTypeViewHolder;
import com.example.eventplanner.dto.eventtype.GetEventTypeDTO;
import com.example.eventplanner.fragments.eventtype.EventTypeEditFragment;
import com.example.eventplanner.fragments.eventtype.EventTypeViewFragment;

import java.util.ArrayList;
import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeViewHolder> {
    private List<GetEventTypeDTO> eventTypeList;

    public EventTypeAdapter(List<GetEventTypeDTO> eventTypeList) {
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
        GetEventTypeDTO eventType = eventTypeList.get(position);

        holder.idTextView.setText(String.valueOf(eventType.getId()));
        holder.nameTextView.setText(eventType.getName());


        // Show or hide status and buttons based on expansion state
        if (eventType.getIsExpanded()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
            holder.viewButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.VISIBLE);

            holder.statusTextView.setText(eventType.getIsActive() ? holder.itemView.getContext().getString(R.string.active)
                    : holder.itemView.getContext().getString(R.string.inactive));

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
            eventType.setIsExpanded(!eventType.getIsExpanded());
            notifyItemChanged(position);
        });


        // Set click listeners for the buttons
        holder.viewButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventTypeName", eventType.getName());
            args.putString("eventTypeDescription", eventType.getDescription());
            args.putStringArrayList("suggestedCategoryNames", new ArrayList<>(eventType.getSuggestedCategoryNames()));

            EventTypeViewFragment fragment = new EventTypeViewFragment();
            fragment.setArguments(args);

            fragment.show(
                    ((AppCompatActivity) v.getContext()).getSupportFragmentManager(),
                    "eventTypeViewDialog"
            );
        });



        holder.editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("eventTypeId", eventType.getId());
            args.putString("eventTypeName", eventType.getName());
            args.putString("eventTypeDescription", eventType.getDescription());
            args.putStringArrayList("suggestedCategoryNames", new ArrayList<>(eventType.getSuggestedCategoryNames()));
            args.putBoolean("isActive", eventType.getIsActive());

            EventTypeEditFragment fragment = new EventTypeEditFragment();
            fragment.setArguments(args);

            fragment.show(
                    ((AppCompatActivity) v.getContext()).getSupportFragmentManager(),
                    "eventTypeEditDialog"
            );
        });


    }

    @Override
    public int getItemCount() {
        return eventTypeList.size();
    }

}
