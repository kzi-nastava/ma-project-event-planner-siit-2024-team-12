package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

public class SolutionFilterViewHolder extends RecyclerView.ViewHolder {

    public TextView parameterName;
    public ImageView expandArrow;
    public RecyclerView options;



    public SolutionFilterViewHolder(@NonNull View itemView) {
        super(itemView);
        parameterName = itemView.findViewById(R.id.parameterName);
        expandArrow = itemView.findViewById(R.id.expandArrow);
        options = itemView.findViewById(R.id.options);

    }
}
