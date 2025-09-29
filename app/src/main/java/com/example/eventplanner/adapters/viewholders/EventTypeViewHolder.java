package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

public class EventTypeViewHolder extends RecyclerView.ViewHolder {
    public TextView idTextView, nameTextView, statusTextView;
    public ImageView expandArrow;
    public Button viewButton, editButton;

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
