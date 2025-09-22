package com.example.eventplanner.adapters.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.budget.GetBudgetItemDTO;

public class BudgetItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView itemNameTextView;
    private final TextView itemDescriptionTextView;
    private final TextView itemCategoryTextView;
    // ... ostale komponente

    public BudgetItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemNameTextView = itemView.findViewById(R.id.tv_item_name);
        itemDescriptionTextView = itemView.findViewById(R.id.tv_item_description);
        itemCategoryTextView = itemView.findViewById(R.id.tv_item_category_display);
    }

    public void bind(GetBudgetItemDTO item) {
        itemNameTextView.setText(item.getName());
        if (item.getCost() != null) {
            itemDescriptionTextView.setText(String.format("Trošak: %.2f", item.getCost()));
        } else {
            itemDescriptionTextView.setText("Trošak: Nije definisan");
        }
        if (item.getCategory() != null) {
            itemCategoryTextView.setText(item.getCategory().getName());
        } else {
            itemCategoryTextView.setText("Kategorija: Nije definisana");
        }

        itemCategoryTextView.setVisibility(View.VISIBLE);
    }
}