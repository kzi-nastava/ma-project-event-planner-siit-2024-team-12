package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView idTextView, nameTextView, descriptionTextView, statusTextView;
    public ImageView expandArrow;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        idTextView = itemView.findViewById(R.id.categoryId);
        nameTextView = itemView.findViewById(R.id.categoryName);
        descriptionTextView = itemView.findViewById(R.id.categoryDescription);
        statusTextView = itemView.findViewById(R.id.categoryStatus);
        expandArrow = itemView.findViewById(R.id.expandArrow);

    }
}
