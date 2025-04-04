package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

public class AgendaViewHolder extends RecyclerView.ViewHolder {

    public TextView activityTime, activityName, activityDescription, activityVenue;
    public ImageView expandArrow;
    public Button editButton, deleteButton;
    public LinearLayout buttonLayout;

    public AgendaViewHolder(@NonNull View itemView) {
        super(itemView);
        activityTime = itemView.findViewById(R.id.activityTime);
        activityName = itemView.findViewById(R.id.activityName);
        activityDescription = itemView.findViewById(R.id.activityDescription);
        activityVenue = itemView.findViewById(R.id.activityVenue);
        expandArrow = itemView.findViewById(R.id.expandArrow);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        buttonLayout = itemView.findViewById(R.id.buttonLayout);
    }
}
